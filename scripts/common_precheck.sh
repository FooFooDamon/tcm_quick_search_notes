if [ $# -lt 1 ]
then
	echo "Usage: $0 <path to .db file>" >&2
	echo "Example: $0 /home/foo/中药速查笔记.db" >&2
	exit 1
fi

DB_FILE="$1"

if [ ! -f "$DB_FILE" ]
then
	echo "File does not exist: $DB_FILE" >&2
	exit 1
fi

DB_CMD=sqlite3

printf "select count(1) from medicine_items;\n.quit\n" | $DB_CMD "$DB_FILE" > /dev/null
medicine_table_test_flag=$?

printf "select count(1) from prescription_items;\n.quit\n" | $DB_CMD "$DB_FILE" > /dev/null
prescription_table_test_flag=$?

if [ $medicine_table_test_flag -ne 0 ] || [ $prescription_table_test_flag -ne 0 ]
then
	echo "Incorrect .db file: $DB_FILE" >&2
	exit 1
fi

echo ".db file: $DB_FILE"

UPDATE_MEDICINE_SQL_FILE=update_medicine_items.sql
UPDATE_PRESCRIPTION_SQL_FILE=update_prescription_items.sql
INSERT_MEDICINE_SQL_FILE=insert_medicine_items.sql
INSERT_PRESCRIPTION_SQL_FILE=insert_prescription_items.sql

CUR_TIME="$(date "+%Y-%m-%d %H:%M:%S")"
BEGIN_DELIM=$(echo "--======================== $CUR_TIME begin ========================--")
END_DELIM=$(echo "--======================== $CUR_TIME end ========================--")

cd $(dirname $0)

