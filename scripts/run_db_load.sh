
#!/bin/bash
# Set the JAVA_HOME environment variable to the path of your Java installation

# Run the Java class
#java -classpath /home/rgddata/pipelines/text-mining-tools/build/libs/text-mining-tools.jar:/home/rgddata/pipelines/text-mining-tools/build/libs/dev-pubmedbert-textminingtools.jar edu.mcw.rgd.nlp.PubMedBertAnnotator "/data/ai"
# Run the Java class
java -Dspring.config=/home/rgdpub/properties/default_db2.xml -classpath $1/text-mining-tools/lib/*:$1/text-mining-tools/build/libs/text-mining-tools.jar edu.mcw.rgd.nlp.PubMedBertLoader "$2" "$3"
