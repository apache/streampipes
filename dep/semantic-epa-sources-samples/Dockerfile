FROM java:8
EXPOSE 8089
EXPOSE 61616

ADD ./de.fzi.cep.sepa/semantic-epa-sources-samples/0.0.1-SNAPSHOT/semantic-epa-sources-samples-0.0.1-SNAPSHOT.jar  /semantic-epa-sources-samples.jar

ENTRYPOINT ["java", "-jar", "/semantic-epa-sources-samples.jar"]
