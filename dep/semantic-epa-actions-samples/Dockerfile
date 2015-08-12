FROM java:8
EXPOSE 8091

ADD ./lib/de.fzi.cep.sepa/semantic-epa-actions-samples/semantic-epa-actions-samples-0.0.1-SNAPSHOT.jar  /semantic-epa-actions-samples.jar

ENTRYPOINT ["java", "-jar", "/semantic-epa-actions-samples.jar"]
