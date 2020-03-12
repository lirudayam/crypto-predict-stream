#!/bin/bash

nohup bin/kafka-server-stop.sh

nohup bin/zookeeper-server-start.sh -daemon config/zookeeper.properties > /dev/null 2>&1 &
sleep 2
nohup bin/kafka-server-start.sh -daemon config/server.properties > /dev/null 2>&1 &
sleep 2

# shellcheck disable=SC2230
if which xdg-open > /dev/null
then
  xdg-open http://localhost:8080
elif which gnome-open > /dev/null
then
  gnome-open http://localhost:8080
fi