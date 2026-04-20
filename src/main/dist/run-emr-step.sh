#!/bin/bash
set -e
export HADOOP_CLASSPATH=$(hbase mapredcp):$(hbase classpath):$HADOOP_CLASSPATH
export HADOOP_HEAPSIZE=8192
JAR=/tmp/ontomate-tools-7.12-all.jar
aws s3 cp s3://emr-version-7/ontomate-tools-7.12-all.jar $JAR
hadoop jar $JAR "$@"
