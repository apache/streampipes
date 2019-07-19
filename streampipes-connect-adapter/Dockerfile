FROM anapsix/alpine-java:8

ENV CONSUL_LOCATION consul

EXPOSE 8099

COPY target/streampipes-connect-adapter.jar  /streampipes-connect-adapter.jar

ENTRYPOINT ["java", "-jar", "/streampipes-connect-adapter.jar"]
