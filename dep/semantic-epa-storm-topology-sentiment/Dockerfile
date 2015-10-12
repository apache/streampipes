FROM anapsix/alpine-java

EXPOSE 8093

RUN wget http://mirror.netcologne.de/apache.org/storm/apache-storm-0.9.5/apache-storm-0.9.5.tar.gz
RUN	tar -zxvf apache-storm-0.9.5.tar.gz

ADD ./de.fzi.cep.sepa/semantic-epa-storm-topology-sentiment-new/0.0.1-SNAPSHOT/semantic-epa-storm-topology-sentiment-new-0.0.1-SNAPSHOT.jar  /semantic-epa-storm-topology-sentiment.jar

ENTRYPOINT ["java", "-jar", "/semantic-epa-storm-topology-sentiment.jar"]
