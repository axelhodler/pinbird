#!/bin/sh

echo -e "run server on PORT: $1 and\nMongoClientURI: $2"

export PORT=$1
export MONGO_URI=$2

mvn exec:java -Dexec.mainClass="earth.xor.Main"
