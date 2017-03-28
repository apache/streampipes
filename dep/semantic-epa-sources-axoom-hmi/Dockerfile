FROM anapsix/alpine-java

EXPOSE 8070

ADD ./de.fzi.cep.sepa/semantic-epa-sources-axoom-hmi/0.40.3-SNAPSHOT/semantic-epa-sources-axoom-hmi-0.40.3-SNAPSHOT.jar  /semantic-epa-sources-axoom-hmi.jar

ENTRYPOINT ["java", "-jar", "/semantic-epa-sources-axoom-hmi.jar"]
