#!/bin/bash

# shut down running systemse
cd kafka
echo 'Shutdown running servers'
nohup bin/kafka-server-stop.sh > /dev/null 2>&1 &
nohup bin/zookeeper-server-stop.sh > /dev/null 2>&1 &
sleep 1

# start up Kafka and Zookeeper
echo 'Start Apache Zookeeper'
nohup bin/zookeeper-server-start.sh -daemon config/zookeeper.properties > /dev/null 2>&1 &
sleep 2
echo 'Start Apache Kafka'
nohup bin/kafka-server-start.sh -daemon config/server.properties > /dev/null 2>&1 &
sleep 2

cd ../
# build and start application
mvn spring-boot:run &
    ( echo "Waiting... Tomcat to launch on 8080..."

    while ! nc -z localhost 8080; do
      sleep 0.5 # wait for 1/10 of the second before check again
    done

    echo "Tomcat launched" &&
    open "http://localhost:8080" )
