#!/bin/bash
#
# ./run_console_client.sh <client_url> <server_url>
#

CLASS_PATH=./bin

java -Djava.security.policy=./security.policy -Djava.rmi.server.codebase=file:${CLASS_PATH} -cp ${CLASS_PATH} enshare.client.ConsoleClient $1 $2
