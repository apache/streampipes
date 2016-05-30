FROM anapsix/alpine-java
EXPOSE 8090

RUN echo 'hosts: files mdns4_minimal [NOTFOUND=return] dns mdns4' >> /etc/nsswitch.conf

ADD ./de.fzi.cep.sepa/semantic-epa-esper/0.0.2-SNAPSHOT/semantic-epa-esper-0.0.2-SNAPSHOT.jar /semantic-epa-esper.jar

ENTRYPOINT ["java", "-Xms8G", "-Xmx8G", "-jar", "/semantic-epa-esper.jar"]
