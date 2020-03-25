FROM ubuntu:20.04
MAINTAINER Leo Irudayam

RUN mkdir -p /kafka
RUN mkdir -p /python

COPY ./kafka/ /kafka/
COPY ./python/ /python/
#RUN rm /kafka/logs/*

# Install "software-properties-common" (for the "add-apt-repository")
RUN apt-get update && apt-get install -y \
    software-properties-common

# Install OpenJDK-8
RUN apt-get update && \
    apt-get install -y openjdk-8-jdk && \
    apt-get install -y ant && \
    apt-get clean;

# Fix certificate issues
RUN apt-get update && \
    apt-get install ca-certificates-java && \
    apt-get clean && \
    update-ca-certificates -f;

# Setup JAVA_HOME -- useful for docker commandline
ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64/
RUN export JAVA_HOME

RUN apt-get -y install python3.6
RUN apt-get -y install python3-venv
RUN apt-get -y install curl

COPY ./start.sh /
RUN chmod +x start.sh

RUN curl https://bootstrap.pypa.io/get-pip.py -o get-pip.py && \
    python3 get-pip.py

RUN pip install pandas && \
    pip install xgboost && \
    apt-get -y install libgomp1 && \
    pip install sklearn

COPY ./target/spring-websockets.jar /

EXPOSE 8080
CMD ./start.sh
