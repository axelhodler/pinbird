#!/usr/bin/zsh
# Call this script with one parameter for the PORT variable, for example:
# ./run_tests.sh 5000

source ~/.zshrc

if [ -z "$1" ]; then
    echo -e "please provide the port as a parameter.\nExample usage:\n./run_tests.sh 1337"
    exit 0
fi

echo -e "run tests with serverport: $1\nEmbedded Mongo Port: $2\nUri: $3\n"

export PORT=$1
# The port used for the EmbeddedMongo instance eg: "12345"
export MONGO_PORT=$2
# The URI for the MongoClient eg: "mongodb://localhost:12345"
export MONGO_URI=$3

eval "mvn test"
