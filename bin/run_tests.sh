#!/bin/sh

export PORT=5000
export MONGO_PORT=12345
export MONGO_URI=mongodb://localhost:12345
export DB_NAME=test

mvn test
