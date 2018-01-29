#!/bin/bash

source $(dirname $0)/common_precheck.sh

for i in $UPDATE_MEDICINE_SQL_FILE $UPDATE_PRESCRIPTION_SQL_FILE $INSERT_MEDICINE_SQL_FILE $INSERT_PRESCRIPTION_SQL_FILE
do
	if [ ! -f $i ]
	then
		echo "Required SQL file does not exist: $i" >&2
		exit 1
	fi
	echo "SQL file validation successful: $i, executing it now ..."
	echo "printf \".quit\\n\" | $DB_CMD -init $i \"$DB_FILE\""
	echo "Done!"
done

