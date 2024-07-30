
#!/bin/bash
# Set the JAVA_HOME environment variable to the path of your Java installation

# Run the Java class
#java -classpath /home/rgddata/pipelines/text-mining-tools/build/libs/text-mining-tools.jar:/home/rgddata/pipelines/text-mining-tools/build/libs/dev-pubmedbert-textminingtools.jar edu.mcw.rgd.nlp.PubMedBertAnnotator "/data/ai"
# Run the Java class
./run.sh "/home/rgdpub/pipelines" "/data/ai" "/data/pubmed/2024" 1