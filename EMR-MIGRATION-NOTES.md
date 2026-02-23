# Text-Mining-Tools: EMR 7.12 Migration Notes

## Project Overview
- Java project for PubMed/NCBI text mining with Hadoop MapReduce and HBase on AWS EMR
- Build: Gradle + Shadow plugin (fat JAR), no `application` plugin needed
- Deploys to EMR 7.12 (Hadoop 3.4.x, Java 17, HBase)

## Key File Locations
- EMR runner script: `src/main/dist/run-emr-step.sh` (generic for all steps)
- HBase loaders: `src/main/java/edu/mcw/rgd/common/mapreduce/ncbi/pubmed/HBase*Loader.java`
- HBase utils: `src/main/java/edu/mcw/rgd/common/utils/HbaseUtils.java`
- Annotators: `src/main/java/edu/mcw/rgd/nlp/utils/ncbi/Distributed*Annotator.java`
- Custom JARs in lib/: gate.jar, gate-asm, gate-compiler-jdt, gnat.jar (not on Maven Central)

---

## EMR Step Configuration

### Problem
EMR 7.12 Custom JAR steps don't include HBase on the classpath. Running `hadoop jar` directly causes `ClassNotFoundException` for HBase classes.

### Solution
Use a shell script step via `command-runner.jar` that sets `HADOOP_CLASSPATH` before running hadoop.

### Generic Runner Script (`run-emr-step.sh`)
```bash
#!/bin/bash
set -e
export HADOOP_CLASSPATH=$(hbase mapredcp):$(hbase classpath):$HADOOP_CLASSPATH
JAR=/tmp/ontomate-tools-7.12-all.jar
[ -f $JAR ] || aws s3 cp s3://emr-version-7/ontomate-tools-7.12-all.jar $JAR
hadoop jar $JAR "$@"
```

- `hbase mapredcp` — adds MapReduce-specific HBase JARs (TableMapReduceUtil, TableInputFormat, etc.)
- `hbase classpath` — adds HBase client JARs (HBaseConfiguration, Connection, etc.)
- Both are needed; `hbase classpath` alone misses MapReduce classes

### EMR Console Configuration
- **Step type:** Shell script (or Custom JAR with `command-runner.jar`)
- **Script/JAR location:** `s3://emr-version-7/run-emr-step.sh`
- **Arguments:** `<fully.qualified.MainClass> <arg1> <arg2> ...`

### Example Steps
1. **HBaseLoader:** `edu.mcw.rgd.common.mapreduce.ncbi.pubmed.HBaseLoader s3://emr-version-7/pubmed pubmed`
2. **DistributedAnnotator:** `edu.mcw.rgd.nlp.utils.ncbi.DistributedAnnotator pubmed s3://emr-repository/Gate/Other_ontologies_9.0.zip yes o Ontologies`

### Why Not Bundle HBase in Fat JAR?
- `implementation` (non-shaded) → protobuf version conflict (`NoSuchMethodError` in `org.apache.hbase.thirdparty.com.google.protobuf`)
- `implementation` (shaded `hbase-shaded-mapreduce`) → classpath conflict with EMR's own HBase, causes silent write failures (table created but 0 rows)
- `compileOnly` + `$(hbase mapredcp):$(hbase classpath)` → uses EMR's own consistent HBase, no conflicts

---

## build.gradle Dependency Notes

### Plugin Setup
- `java` + `com.github.johnrengelman.shadow:7.1.2` — NO `application` plugin
- Shadow plugin without `application` avoids the `mainClass` property error
- No `Main-Class` in manifest — each EMR step specifies its own main class via `hadoop jar <jar> <mainClass>`

### Dependency Scopes

#### `implementation` (bundled in fat JAR)
- Apache Commons (math3, lang3, collections4, io, text, pool2)
- Jackson 1.x Codehaus (jackson-core-asl, jackson-mapper-asl)
- Jackson 2.x FasterXML (jackson-databind:2.8.7 — transitively brings core + annotations)
- JSON (org.json:json:20200518, net.sf.json-lib:json-lib:2.2:jdk13)
- Solr client (solr-solrj:8.6.2)
- Jsoup, Jersey, AOP (aopalliance, aspectj)
- Apache MIME4J, Ant
- SLF4J (api + simple)
- Custom JARs via `fileTree(dir: 'lib')` — gate.jar, gnat.jar, gate-asm, gate-compiler-jdt

#### `compileOnly` (EMR provides at runtime)
- Hadoop: hadoop-client, hadoop-mapreduce-client-core, hadoop-mapreduce-client-jobclient (all 3.4.1)
- HBase: hbase-shaded-mapreduce:2.6.2, hbase-server:2.6.2

### lib/ Folder Cleanup (Feb 2025)
- Reduced from 66 JARs to 4 custom JARs
- Moved all Maven Central-available deps to build.gradle
- Removed 16+ completely unused libraries (tika, pdfbox, lucene, httpcache4j, plexus, ivy, restlet, woodstox, xstream, xmlbeans, efetch, eutils, etc.)
- Only gate (3 JARs) and gnat (1 JAR) remain — not available on Maven Central

---

## PubMed XML Format Change
- Old eFetch SOAP responses use `ns1:` prefix: `<ns1:PubmedArticle>`, `<ns1:PMID>`
- Newer PubMed baseline XML has no prefix: `<PubmedArticle>`, `<PMID>`
- Regex patterns updated to handle both via `(?:ns1:)?` optional group
- Affected file: `HBaseLoader.java` lines 29-30

---

## Windows to Linux Script Pitfalls
- Files created on Windows have CRLF (`\r\n`) line endings — causes "No such file or directory" error on Linux
- Windows bash escapes `!` in shebang even in single quotes → `#\!/bin/bash` instead of `#!/bin/bash`
- Fix: write with `printf '%s\n' ...`, then `sed -i 's/#\\!/#!/' script.sh`, then verify with `xxd`
