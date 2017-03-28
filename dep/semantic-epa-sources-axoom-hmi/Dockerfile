FROM anapsix/alpine-java

EXPOSE 8070

ADD ./de.fzi.cep.sepa/semantic-epa-sources-axoom-hmi/0.0.2-SNAPSHOT/semantic-epa-sources-axoom-hmi-0.0.2-SNAPSHOT.jar  /semantic-epa-sources-axoom-hmi.jar

ENTRYPOINT ["java", "-jar", "/semantic-epa-sources-axoom-hmi.jar"]
