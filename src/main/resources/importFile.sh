#! /bin/bash

#Importing data with .import of sqlite after creating a new table and then adding an index to the table
{
sqlite3 SNPGRAPH.db ".mode tabs" "CREATE TABLE chipseq (id INTEGER PRIMARY KEY AUTOINCREMENT, chr VARCHAR(5) not NULL, start INTEGER not NULL, end INTEGER not NULL, peak INTEGER NOT NULL);" ".import chipSeqDataSortIndex.txt chipseq" "CREATE INDEX chipseq_pos ON chipseq (chr, start, end)" ".quit" &
}




exit 0
