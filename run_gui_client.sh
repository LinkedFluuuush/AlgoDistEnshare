#!/bin/bash
#
# ./run_client.sh <client_url> <server_url>
#

CLASS_PATH=./build/classes

java -Djava.security.policy=./security.policy -Djava.rmi.server.codebase=file:${CLASS_PATH} -cp ${CLASS_PATH} enshare.client.GuiClient $1 $2
