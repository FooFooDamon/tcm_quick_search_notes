#!/bin/bash

source $(dirname $0)/common_precheck.sh

INSERT_MEDICINE_DATA_FILE=insert_medicine_items.data.txt
INSERT_PRESCRIPTION_DATA_FILE=insert_prescription_items.data.txt

for i in $INSERT_MEDICINE_DATA_FILE $INSERT_PRESCRIPTION_DATA_FILE
do
	if [ ! -f $i ]
	then
		echo "Required data file does not exist: $i" >&2
		exit 1
	fi
	echo "Data file validation successful: $i"
done

UPDATE_MEDICINE_DATA_FILE=update_medicine_items.data.txt
UPDATE_PRESCRIPTION_DATA_FILE=update_prescription_items.data.txt

QUERY_UNEDITED_MEDICINE_NAMES_SQL="select name from medicine_items\n\
	where (effects is null or length(effects) = 0)\n\
	and (actions_and_indications is null or length(actions_and_indications) = 0)\n\
	and (alias is null or length(alias) = 0)"

printf "$QUERY_UNEDITED_MEDICINE_NAMES_SQL;\n.quit\n" | $DB_CMD "$DB_FILE" > $UPDATE_MEDICINE_DATA_FILE
[ $? -eq 0 ] || exit 1
echo "Data file generation successful: $UPDATE_MEDICINE_DATA_FILE"

QUERY_UNEDITED_PRESCRIPTION_NAMES_SQL="select name from prescription_items\n\
	where (effects is null or length(effects) = 0)\n\
	and (actions_and_indications is null or length(actions_and_indications) = 0)\n\
	and (alias is null or length(alias) = 0)"

printf "$QUERY_UNEDITED_PRESCRIPTION_NAMES_SQL;\n.quit\n" | $DB_CMD "$DB_FILE" > $UPDATE_PRESCRIPTION_DATA_FILE
[ $? -eq 0 ] || exit 1
echo "Data file generation successful: $UPDATE_PRESCRIPTION_DATA_FILE"

UPDATE_MEDICINE_ITEM_SQL="update medicine_items\n\
set alias = '',\n\
	effects = '',\n\
	actions_and_indications = '',\n\
	details = '',\n\
	common_prescriptions = '',\n\
	common_partners = '',\n\
	similar_medicines = '',\n\
	dosage_reference = '一般用量为X～X克，重症时可用X～X克',\n\
	contraindications = '',\n\
	remarks = ''\n\
where (effects is null or length(effects) = 0)\n\
	and (actions_and_indications is null or length(actions_and_indications) = 0)\n\
	and (alias is null or length(alias) = 0)"

UPDATE_PRESCRIPTION_ITEM_SQL="update prescription_items\n\
set alias = '',\n\
	effects = '',\n\
	actions_and_indications = '',\n\
	contraindications = '',\n\
	relative_prescriptions = '',\n\
	remarks = ''\n\
where (effects is null or length(effects) = 0)\n\
	and (actions_and_indications is null or length(actions_and_indications) = 0)\n\
	and (alias is null or length(alias) = 0)"

UPDATE_SQLS=("$UPDATE_MEDICINE_ITEM_SQL" "$UPDATE_PRESCRIPTION_ITEM_SQL")

UPDATE_DATA_FILES=($UPDATE_MEDICINE_DATA_FILE $UPDATE_PRESCRIPTION_DATA_FILE)
UPDATE_SQL_FILES=($UPDATE_MEDICINE_SQL_FILE $UPDATE_PRESCRIPTION_SQL_FILE)
UPDATE_FILE_COUNT=${#UPDATE_DATA_FILES[*]}

for i in `seq 0 $(($UPDATE_FILE_COUNT - 1))`
do
	data_file=${UPDATE_DATA_FILES[$i]}
	sql_file=${UPDATE_SQL_FILES[$i]}

	echo "" > $sql_file

	while read line
	do
		update_sql="${UPDATE_SQLS[$i]}\n\tand name = '$line';"
		printf "${update_sql}\n\n" >> $sql_file
	done < $data_file
	echo "SQL file generation successful: $sql_file"
done

INSERT_MEDICINE_ITEM_SQL="insert into medicine_items(name, alias, category, effects, actions_and_indications,\n\
	details, common_prescriptions, common_partners, similar_medicines, dosage_reference,\n\
	contraindications, remarks)\n\
values(/* name: */ '__NEW_ITEM_NAME__',\n\
	/* alias: */ '',\n\
	/* category: */ 0,\n\
	/* effects: */ '',\n\
	/* actions_and_indications: */ '',\n\
	/* details: */ '',\n\
	/* common_prescriptions: */ '',\n\
	/* common_partners: */ '',\n\
	/* similar_medicines: */ '',\n\
	/* dosage_reference: */ '一般用量为X～X克，重症时可用X～X克',\n\
	/* contraindications: */ '',\n\
	/* remarks: */ '' );"

INSERT_PRESCRIPTION_ITEM_SQL="insert into prescription_items(name, alias, category, effects, actions_and_indications,\n\
	contraindications, relative_prescriptions, remarks)\n\
values(/* name: */ '__NEW_ITEM_NAME__',\n\
	/* alias: */ '',\n\
	/* category: */ 0,\n\
	/* effects: */ '',\n\
	/* actions_and_indications: */ '',\n\
	/* contraindications: */ '',\n\
	/* relative_prescriptions: */ '',\n\
	/* remarks: */ '' );"

INSERT_SQLS=("$INSERT_MEDICINE_ITEM_SQL" "$INSERT_PRESCRIPTION_ITEM_SQL")

INSERT_DATA_FILES=($INSERT_MEDICINE_DATA_FILE $INSERT_PRESCRIPTION_DATA_FILE)
INSERT_SQL_FILES=($INSERT_MEDICINE_SQL_FILE $INSERT_PRESCRIPTION_SQL_FILE)
INSERT_FILE_COUNT=${#INSERT_DATA_FILES[*]}

for i in `seq 0 $(($INSERT_FILE_COUNT - 1))`
do
	data_file=${INSERT_DATA_FILES[$i]}
	sql_file=${INSERT_SQL_FILES[$i]}

	echo "" > $sql_file

	while read line
	do
		insert_sql="${INSERT_SQLS[$i]/__NEW_ITEM_NAME__/$line}"
		printf "${insert_sql}\n\n" >> $sql_file
	done < $data_file
	echo "SQL file generation successful: $sql_file"
done

