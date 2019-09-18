FROM adoptopenjdk/openjdk8-openj9:alpine-slim

ENV CONSUL_LOCATION consul

EXPOSE 8099

COPY target/streampipes-connect-adapter.jar  /streampipes-connect-adapter.jar

ENTRYPOINT ["java", "-jar", "/streampipes-connect-adapter.jar"]
