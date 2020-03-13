# Cryptocurrency prediction stream with Kafka

This Spring Boot Kotlin project takes the stored and old data of cryptocurrency prices and simulates a stream.
For starting the initial load you'd need to call (skip when using new version):

```
http://localhost:8080/kafka/trigger
```

For visualisation purposes there is a web user interface. This works on an Apache Tomcat server and it consumes a web socket stream.
Data is in the first step read entirely from the CSV file and grouped by crypto currency. 
When this has been completed the Web UI is ready, you can trigger a simulation stream to start. This stream is pushing every second the prices of all available cryptocurrencies to the timestamp X.
The cronjob simulates every second a day from history.

You can anytime stop or continue the stream.
The webUI runs at:
```
http://localhost:8080
```

The stream is been consumed via Apache Flink in combination with Apache Kafka. There are multiple algorithms applied during the stream processing, defined in the RoleFactory:
* Rule 1: When the price drops or increases by >20%, an anomaly is detected
* Rule 2: The last 10 days get compared and when the spread is outter 3-sigma from distribution, an anomaly is detected
* Rule 3: Rule 2 with close date


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

The Kafka and Zookeeper distribution are attached in this GitHub repo. The prepare.sh file is NOT yet finished.