FROM ubuntu:14.04

RUN apt-get update
RUN apt-get upgrade -y

RUN apt-get install -y wget
RUN apt-get install -y openjdk-7-jre  


RUN wget http://artfiles.org/apache.org/kafka/0.8.2.0/kafka_2.10-0.8.2.0.tgz

RUN tar -zxvf kafka_2.10-0.8.2.0.tgz

RUN ls
EXPOSE 9092


ENTRYPOINT ["./kafka_2.10-0.8.2.0/bin/kafka-server-start.sh", "./kafka_2.10-0.8.2.0/config/server.properties"]
