#!/bin/bash
while read -r line; do
    modified_line=$(echo "$line" | sed 's/,GET/ +0800,GET/')
    modified_line=$(echo "$modified_line" | sed 's/,POST/ +0800,POST/')
    echo "$modified_line"
done < weblog.csv > weblog_update.csv