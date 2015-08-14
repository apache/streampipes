FROM anapsix/alpine-java
EXPOSE 8090

ADD ./de.fzi.cep.sepa/semantic-epa-esper/0.0.1-SNAPSHOT/semantic-epa-esper-0.0.1-SNAPSHOT.jar /semantic-epa-esper.jar

ENTRYPOINT ["java", "-jar", "/semantic-epa-esper.jar"]
