FROM adoptopenjdk/openjdk8-openj9:alpine-slim

ENV CONSUL_LOCATION consul

EXPOSE 8099

COPY target/streampipes-connect-container-master.jar  /streampipes-connect-container-master.jar

ENTRYPOINT ["java", "-jar", "/streampipes-connect-container-master.jar"]
