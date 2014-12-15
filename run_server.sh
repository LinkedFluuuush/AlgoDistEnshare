#!/bin/bash
#
# ./run_server.sh <server_url> <directory>
#

CLASS_PATH=./bin

killall rmiregistry 2> /dev/null
sleep 1
cd ${CLASS_PATH}
rmiregistry &
cd - > /dev/null
java -Djava.security.policy=./security.policy -Djava.rmi.server.codebase=file:${CLASS_PATH} -cp ${CLASS_PATH} enshare.server.Server $1 $2
killall rmiregistry
sleep 1
