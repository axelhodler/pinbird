#!/bin/bash
# Call this script with one parameter for the PORT variable, for example:
# ./run_tests.sh 5000

if [ -z "$1" ]; then
    echo -e "please provide the port as a parameter.\nExample usage:\n./run_tests.sh 1337"
    exit 0
fi

echo "run tests with port $1"

export PORT=$1
# The port used for the EmbeddedMongo instance eg: "12345"
export MONGO_PORT=$2
# The URI for the MongoClient eg: "mongodb://localhost:"
export URI_BASE=$3

mvn test
