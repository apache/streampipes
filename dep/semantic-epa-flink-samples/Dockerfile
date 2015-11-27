FROM anapsix/alpine-java

EXPOSE 8094

#RUN apk add python
RUN wget "http://www.eu.apache.org/dist/flink/flink-0.10.0/flink-0.10.0-bin-hadoop1-scala_2.10.tgz"
RUN	tar -zxvf flink-0.10.0-bin-hadoop1-scala_2.10.tgz

ADD ./de.fzi.cep.sepa/semantic-epa-flink-samples/0.0.1-SNAPSHOT/semantic-epa-flink-samples-0.0.1-SNAPSHOT.jar  /semantic-epa-flink-samples-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "/semantic-epa-flink-samples-0.0.1-SNAPSHOT.jar"]
