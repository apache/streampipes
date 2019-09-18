FROM adoptopenjdk/openjdk8-openj9:alpine-slim

EXPOSE 8030
ENV CONSUL_LOCATION consul

ADD ./target/streampipes-backend.jar  /streampipes-backend.jar

ENTRYPOINT ["java", "-jar", "/streampipes-backend.jar"]
