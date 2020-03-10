# crypto-predict-stream
 Cryptocurrency prediction stream with Kafka

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
```

## Running the project locally

This Maven project can run easily using mvn spring-boot:run. However, it could be that you need to run a clean install, to empty build caches.
