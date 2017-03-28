FROM anapsix/alpine-java

EXPOSE 8094
RUN echo 'hosts: files mdns4_minimal [NOTFOUND=return] dns mdns4' >> /etc/nsswitch.conf

ADD ./de.fzi.cep.sepa/semantic-epa-flink-samples/0.40.3-SNAPSHOT/semantic-epa-flink-samples-0.40.3-SNAPSHOT.jar  /semantic-epa-flink-samples-0.40.3-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "/semantic-epa-flink-samples-0.40.3-SNAPSHOT.jar"]
