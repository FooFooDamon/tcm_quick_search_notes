#!/bin/bash

source $(dirname $0)/common_precheck.sh

for i in $UPDATE_MEDICINE_SQL_FILE $UPDATE_PRESCRIPTION_SQL_FILE $INSERT_MEDICINE_SQL_FILE $INSERT_PRESCRIPTION_SQL_FILE
do
	if [ -f $i ]
	then
		echo "Executing SQL file: $i"
		echo "printf \".quit\\n\" | $DB_CMD -init $i \"$DB_FILE\""
		time printf ".quit\\n" | $DB_CMD -init $i "$DB_FILE"
		echo "Done!"
	fi
done

