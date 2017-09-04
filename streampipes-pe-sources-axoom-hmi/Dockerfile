FROM anapsix/alpine-java


ENV CONSUL_LOCATION consul

EXPOSE 8090

ADD target/streampipes-pe-sources-axoom-hmi-0.41.2-SNAPSHOT.jar  /streampipes-pe-sources-axoom-hmi.jar

ENTRYPOINT ["java", "-jar", "/streampipes-pe-sources-axoom-hmi.jar"]
