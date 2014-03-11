#!/bin/sh

export PORT=$1
export MONGO_URI=$2
export DB_NAME=$3

echo -e "run server on PORT: $1,\nMongoClientURI: $2,\nDB_NAME: $3\n"

mvn exec:java -Dexec.mainClass="earth.xor.Main"
