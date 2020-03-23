#!/bin/bash

# shut down running systemse
cd kafka
echo 'Start Apache Zookeeper'
nohup bin/zookeeper-server-start.sh -daemon config/zookeeper.properties
sleep 2
echo 'Start Apache Kafka'
nohup bin/kafka-server-start.sh -daemon config/server.properties

sleep 5

echo 'Starting Python Server'
cd ../python
nohup python3 server.py > a.out 2>&1 & disown
cd ../

echo 'Starting Spring Boot App'
nohup java -jar spring-websockets.jar
