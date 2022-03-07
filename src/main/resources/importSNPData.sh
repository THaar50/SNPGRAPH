#! /bin/bash

#Importing data with .import of sqlite after creating a new table and then adding an index to the table
BASEDIR=$(dirname "$0")
{
sqlite3 "$BASEDIR/SNPGRAPH.db" ".mode tabs" "DROP TABLE IF EXISTS snp;" "CREATE TABLE IF NOT EXISTS snp (id INTEGER PRIMARY KEY AUTOINCREMENT, chr_id INTEGER not NULL, chr VARCHAR(5) not NULL, start INTEGER not NULL);" ".import $BASEDIR/snpDataIndex.txt snp" "CREATE INDEX IF NOT EXISTS snp_pos ON snp (chr, start);" ".quit"
}
find "$BASEDIR/snpDataIndex.txt" -delete

exit 0
