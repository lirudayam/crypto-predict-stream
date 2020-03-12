# Cryptocurrency prediction stream with Kafka

This Spring Boot Kotlin project takes the stored and old data of cryptocurrency prices and simulates a stream. Every second is one day and 
anomaly detection triggers when an incoming data point is not in an 80% confidence prediction interval. For starting the initial load you'd need to call:

```
http://localhost:8080/kafka/trigger
```

This loads all the data and formats the right. It will execute also a 50 day from minimum jump start in order to have a better trained model. 
After this it starts the every second cron job to push data into the stream. The ARIMA parameters have been used out of "Bitcoin Price Prediction: An ARIMA Approach".

## Prerequisites for running under Mac

Initially make sure Kafka is installed, and the Apache Zookeeper server where Kafka will run on later (you may need to install a special Java distribution):

```bash
brew install zookeeper
brew install kafka
```

Then you need to start Zookeeper and afterwards Kafka:
```bash
zookeeper-server-start /usr/local/etc/kafka/zookeeper.properties
kafka-server-start /usr/local/etc/kafka/server.properties
```

OR

Download Apache Kafka latest distribution (https://www.apache.org/dyn/closer.cgi?path=/kafka/2.4.0/kafka_2.11-2.4.0.tgz) and run then in two separate terminal windows:

```bash
bin/zookeeper-server-start.sh config/zookeeper.properties
bin/kafka-server-start.sh config/server.properties
```

## Running the project locally

This Maven project can run easily using mvn spring-boot:run. However, it could be that you need to run a clean install, to empty build caches.

To clean the Kafka, run following:

```bash
bin/kafka-topics.sh --zookeeper localhost:2181 --delete --topic users
```
