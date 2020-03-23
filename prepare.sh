#!/bin/bash

# shut down running systemse
cd kafka
echo 'Shutdown running servers'
lsof -ti tcp:8080 | xargs kill
lsof -ti tcp:4444 | xargs kill
nohup bin/kafka-server-stop.sh > /dev/null 2>&1 &
nohup bin/zookeeper-server-stop.sh > /dev/null 2>&1 &
sleep 1

echo 'Clear log file'
rm logs/*

# start up Kafka and Zookeeper
echo 'Start Apache Zookeeper'
nohup bin/zookeeper-server-start.sh -daemon config/zookeeper.properties > /dev/null 2>&1 &
sleep 2
echo 'Start Apache Kafka'
nohup bin/kafka-server-start.sh -daemon config/server.properties > /dev/null 2>&1 &

sleep 10
echo 'Clear Topic'
nohup bin/kafka-topics.sh --zookeeper localhost:2181 --delete --topic cryptocurrency
nohup bin/kafka-streams-application-reset.sh --application-id "li.crypto-stream" --bootstrap-servers localhost:9092 --input-topics cryptocurrency --zookeeper localhost:2181
sleep 5

# shellcheck disable=SC2164
cd ../python
nohup python server.py > nohup.out 2>&1 & disown
cd ../
exit

# build and start application
nohup mvn spring-boot:run &
    ( echo "Waiting... Tomcat to launch on 8080..."

    while ! nc -z localhost 8080; do
      sleep 1
    done

    echo "Tomcat launched" &&
    open "http://localhost:8080" )


# function called by trap
other_commands() {
    echo 'Shutdown triggered'
    lsof -ti tcp:8080 | xargs kill
    nohup kafka/bin/kafka-server-stop.sh > /dev/null 2>&1 &
    sleep 5
    nohup kafka/bin/zookeeper-server-stop.sh > /dev/null 2>&1 &
    echo 'Shutdown complete'
    exit 1
}

trap 'other_commands' SIGINT

input="$@"

while true; do
    printf "\rExecute control C to stop >>> "
    read input
    [[ $input == finish ]] && break
done