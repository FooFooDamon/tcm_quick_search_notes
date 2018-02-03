#!/bin/bash

if [ $# -lt 3 ]
then
	echo "Usage: $0 <start line number> <end line number> <SQL file>" >&2
	echo "Example: $0 1 50 /home/foo/update_medicine_items.sql" >&2
	exit 1
fi

if [ $1 -gt $2 ]
then
	echo "Start line number must less than end line number! Current start line number: $1, end line number: $2" >&2
	exit 1
fi
start_line=$1
end_line=$2

if [ ! -f "$3" ]
then
	echo "File does not exist: $3" >&2
	exit 1
fi
target_file="$3"

sed -i "${start_line},${end_line}s/^--//" "$target_file"

