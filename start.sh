#!/bin/bash

# shut down running systems
# shellcheck disable=SC2164
cd kafka
echo 'Start Apache Zookeeper'
nohup ./bin/zookeeper-server-start.sh -daemon ./config/zookeeper.properties
sleep 2
echo 'Start Apache Kafka'
nohup ./bin/kafka-server-start.sh -daemon ./config/server.properties

sleep 5

echo 'Starting Python Server'
# shellcheck disable=SC2164
cd ../python
nohup python3 server.py > a.out 2>&1 & disown
cd ../

echo 'Starting Spring Boot App'
nohup java -jar spring-websockets.jar
