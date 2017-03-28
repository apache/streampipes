FROM anapsix/alpine-java

EXPOSE 8089
EXPOSE 61616
RUN echo 'hosts: files mdns4_minimal [NOTFOUND=return] dns mdns4' >> /etc/nsswitch.conf

ADD ./de.fzi.cep.sepa/semantic-epa-sources-samples/0.40.3-SNAPSHOT/semantic-epa-sources-samples-0.40.3-SNAPSHOT.jar  /semantic-epa-sources-samples.jar

ENTRYPOINT ["java", "-jar", "/semantic-epa-sources-samples.jar"]
