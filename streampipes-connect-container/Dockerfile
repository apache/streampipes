FROM anapsix/alpine-java:8

ENV CONSUL_LOCATION consul

EXPOSE 8099

COPY target/streampipes-connect-container.jar  /streampipes-connect-container.jar

ENTRYPOINT ["java", "-jar", "/streampipes-connect-container.jar"]
