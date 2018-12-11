#!/bin/sh

FILE_NAME="../build_files/build.xml"
cat $FILE_NAME.head > $FILE_NAME

#ls ../lib/|awk '{print "<pathelement location=\"trunk/lib/" $1 "\"/>"}' >> $FILE_NAME
ls ../lib/|awk '{print "<zipfileset excludes=\"META-INF/*.SF\" src=\"trunk/lib/" $1 "\"/>"}' >> $FILE_NAME
cat $FILE_NAME.tail >> $FILE_NAME
cp $FILE_NAME ../../build.xml
