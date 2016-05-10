FROM anapsix/alpine-java

EXPOSE 8091

RUN echo 'hosts: files mdns4_minimal [NOTFOUND=return] dns mdns4' >> /etc/nsswitch.conf

ADD ./de.fzi.cep.sepa/semantic-epa-actions-samples/0.0.1-SNAPSHOT/semantic-epa-actions-samples-0.0.1-SNAPSHOT.jar  /semantic-epa-actions-samples.jar

ENTRYPOINT ["java", "-jar", "/semantic-epa-actions-samples.jar"]
