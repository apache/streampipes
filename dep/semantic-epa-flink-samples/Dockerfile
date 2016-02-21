FROM anapsix/alpine-java

EXPOSE 8094 6123 48080 48081

#RUN apk add python
RUN wget "http://www.eu.apache.org/dist/flink/flink-0.10.0/flink-0.10.0-bin-hadoop1-scala_2.10.tgz"
RUN	tar -zxvf flink-0.10.0-bin-hadoop1-scala_2.10.tgz
RUN echo 'hosts: files mdns4_minimal [NOTFOUND=return] dns mdns4' >> /etc/nsswitch.conf

ADD /flink-conf.yaml ./flink-0.10.0/conf/flink-conf.yaml
ADD ./de.fzi.cep.sepa/semantic-epa-flink-samples/0.0.1-SNAPSHOT/semantic-epa-flink-samples-0.0.1-SNAPSHOT.jar  /semantic-epa-flink-samples-0.0.1-SNAPSHOT.jar

CMD ["./flink-0.10.0/bin/start-local-streaming.sh"]
ENTRYPOINT ["java", "-jar", "/semantic-epa-flink-samples-0.0.1-SNAPSHOT.jar"]
