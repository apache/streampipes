FROM anapsix/alpine-java

EXPOSE 8088
EXPOSE 61616

ADD ./de.fzi.cep.sepa/semantic-epa-sources-mnist/0.0.2-SNAPSHOT/semantic-epa-sources-mnist-0.0.2-SNAPSHOT.jar  /semantic-epa-sources-mnist.jar

ENTRYPOINT ["java", "-jar", "/semantic-epa-sources-mnist.jar"]