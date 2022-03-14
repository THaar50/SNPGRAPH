#! /bin/bash

# Prepare snpData
echo Preparing SNP sequence.. This might take a minute..
input=$1
BASEDIR=$(dirname "$0")
mv "$1" snp.txt
echo Filtering data..
awk '{print $1 "\t" $2}' snp.txt > snpData.txt
echo Done!
echo Sorting..
sort -V -k1 snpData.txt > snpDataSort.txt
find "snpData.txt" -delete
echo Done!
mv snp.txt "$1"

# Add index column to snpData
echo Adding indices..
awk '{printf("%d\t%s\n", NR,$0)}' snpDataSort.txt > "$BASEDIR/snpDataIndex.txt"
find "snpDataSort.txt" -delete
echo Done!
echo Data ready to use!


exit 0
