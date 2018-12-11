#!/bin/sh

cp `grep zipfileset $1 | awk -F\" '{print $4}'` ../lib
