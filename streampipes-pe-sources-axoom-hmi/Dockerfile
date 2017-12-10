FROM anapsix/alpine-java


ENV CONSUL_LOCATION consul

EXPOSE 8090

ADD target/streampipes-pe-sources-axoom-hmi.jar  /streampipes-pe-sources-axoom-hmi.jar

ENTRYPOINT ["java", "-jar", "/streampipes-pe-sources-axoom-hmi.jar"]
