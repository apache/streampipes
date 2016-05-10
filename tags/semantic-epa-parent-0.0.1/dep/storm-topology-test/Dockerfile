FROM anapsix/alpine-java

EXPOSE 8093

RUN wget http://mirror.netcologne.de/apache.org/storm/apache-storm-0.9.5/apache-storm-0.9.5.tar.gz
RUN	tar -zxvf apache-storm-0.9.5.tar.gz

ADD ./de.fzi.cep.sepa/semantic-epa-storm-topology-sentiment/0.0.1-SNAPSHOT/semantic-epa-storm-topology-sentiment-0.0.1-SNAPSHOT.jar  /semantic-epa-storm-topology-sentiment.jar

#ENTRYPOINT ["./apache-storm-0.9.5/bin/storm", "jar", "/semantic-epa-storm-topology-sentiment.jar", "de.fzi.cep.sepa.storm.sentiment.topology.Main"]
