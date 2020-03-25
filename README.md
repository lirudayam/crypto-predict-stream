# Cryptocurrency prediction stream with Kafka

![Auto Docker Build/Push](https://github.com/lirudayam/crypto-predict-stream/workflows/Auto%20Docker%20Build/Push/badge.svg)

This Spring Boot Kotlin project takes the stored and old data of cryptocurrency prices and simulates a stream.

For visualisation purposes there is a web user interface. This works on an Apache Tomcat server, and it consumes a web socket stream.
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

## Running the application from the existing Docker image
```bash
docker pull leosmashesdocker/crypto-predict-stream:latest
docker run -p 8080:8080 leosmashesdocker/crypto-predict-stream:latest
```

## Prerequisites for running under Mac

* Have an up-to-date Java distribution with working JAVA_HOME env variables
* Have Apache Maven installed otherwise install maven

### Running the project locally from scratch

```bash
./prepare.sh
```

### Build docker image

```bash
mvn clean install spring-boot:repackage
docker build -t crypto-predict-stream .
docker tag crypto-predict-stream leosmashesdocker/crypto-predict-stream
docker push leosmashesdocker/crypto-predict-stream
docker run -p 8080:8080 crypto-predict-stream:latest

```


