FROM anapsix/alpine-java
EXPOSE 8090

RUN echo 'hosts: files mdns4_minimal [NOTFOUND=return] dns mdns4' >> /etc/nsswitch.conf

ADD ./de.fzi.cep.sepa/semantic-epa-esper/0.40.3-SNAPSHOT/semantic-epa-esper-0.40.3-SNAPSHOT.jar /semantic-epa-esper.jar

ENTRYPOINT ["java", "-Xms2G", "-Xmx4G", "-jar", "/semantic-epa-esper.jar"]
