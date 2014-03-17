#!/bin/sh

export PORT=5000
export MONGO_URI=mongodb://localhost:27017
export DB_NAME=test

mvn exec:java -Dexec.mainClass="earth.xor.Main"
