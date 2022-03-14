#! /bin/bash

#Importing data with .import of sqlite after creating a new table and then adding an index to the table
BASEDIR=$(dirname "$0")
DBDIR="$1"
{
sqlite3 "$DBDIR/snpgraph.db" ".mode tabs" "DROP TABLE IF EXISTS chipseq;" "CREATE TABLE chipseq (id INTEGER PRIMARY KEY AUTOINCREMENT, chr VARCHAR(5) not NULL, start INTEGER not NULL, end INTEGER not NULL, peak INTEGER NOT NULL);" ".import $BASEDIR/chipSeqDataSortIndex.txt chipseq" "CREATE INDEX chipseq_pos ON chipseq (chr, start, end);" ".quit"
}
find "$BASEDIR/chipSeqDataSortIndex.txt" -delete



exit 0
