FROM ubuntu:14.04

RUN apt-get update
RUN apt-get upgrade -y

RUN apt-get install -y wget
RUN apt-get install -y openjdk-7-jre  


RUN wget http://ftp.fau.de/apache/zookeeper/zookeeper-3.3.6/zookeeper-3.3.6.tar.gz

RUN tar -zxvf zookeeper-3.3.6.tar.gz

EXPOSE 2181

ADD /zoo.cfg ./zookeeper-3.3.6/conf/zoo.cfg

ENTRYPOINT ["./zookeeper-3.3.6/bin/zkServer.sh", "start-foreground"]
