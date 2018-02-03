source $(dirname $0)/uncomment.sh

sed -i "${start_line},${end_line}s/^/--/" "$target_file"

