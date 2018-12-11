#!/bin/sh

cat $1.head > $1
ls ../lib/|awk '{print "<zipfileset excludes=\"META-INF/*.SF\" src=\"../lib/" $1 "\"/>"}' >> $1
cat $1.tail >> $1
