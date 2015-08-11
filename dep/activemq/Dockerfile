FROM ubuntu:14.04

RUN apt-get update
RUN apt-get upgrade -y

RUN apt-get install -y wget
RUN apt-get install -y openjdk-7-jre  

RUN wget http://mirror.23media.de/apache/activemq/5.11.1/apache-activemq-5.11.1-bin.tar.gz

RUN tar -zxvf apache-activemq-5.11.1-bin.tar.gz

ADD /activemq.xml ./apache-activemq-5.11.1/conf/activemq.xml

EXPOSE 61616


ENTRYPOINT ["./apache-activemq-5.11.1/bin/activemq",  "console"]
