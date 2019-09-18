FROM adoptopenjdk/openjdk8-openj9:alpine-slim

EXPOSE 8090
ENV CONSUL_LOCATION consul

ADD ./target/streampipes-processors-filters-siddhi.jar  /streampipes-processing-element-container.jar

ENTRYPOINT ["java", "-jar", "/streampipes-processing-element-container.jar"]
