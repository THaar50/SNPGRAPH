#! /bin/bash

# Prepare ChIP-Seq Data
echo Preparing ChIP-Seq sequence..
input=$1
mv "$1" chipSeq.txt
echo Using data from "$1"
echo Filtering data..
awk '{print $1 "\t" $2 "\t" $3 "\t" $10}' chipSeq.txt > chipSeqData.txt
echo Done!
echo Sorting..
sort -V -k1 chipSeqData.txt > chipSeqDataSort.txt
find "chipSeqData.txt" -delete
echo Done!
mv chipSeq.txt "$1"

# Add index column to chipSeqData
echo Adding indices..
awk '{printf("%d\t%s\n", NR,$0)}' chipSeqDataSort.txt > chipSeqDataSortIndex.txt
echo Done!
echo Deleting temporary files..
find "chipSeqDataSort.txt" -delete
echo Done!


exit 0
