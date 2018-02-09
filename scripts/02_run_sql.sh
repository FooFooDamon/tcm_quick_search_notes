#!/bin/bash

source $(dirname $0)/common_precheck.sh

for i in $UPDATE_MEDICINE_SQL_FILE $UPDATE_PRESCRIPTION_SQL_FILE $INSERT_MEDICINE_SQL_FILE $INSERT_PRESCRIPTION_SQL_FILE
do
	if [ -f $i ]
	then
		echo "Executing SQL file: $i"
		echo "printf \".quit\\n\" | $DB_CMD -init $i \"$DB_FILE\""
		time printf ".quit\\n" | $DB_CMD -init $i "$DB_FILE"
		line_count=$(wc -l $i | awk '{ print $1 }')
		if [ $line_count -gt 1 ]
		then
			$(dirname $0)/comment.sh 1 $(($line_count - 1)) $i
		fi
		echo "Done!"
	fi
done

