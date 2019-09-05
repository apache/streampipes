FROM openjdk:8-jre-alpine

EXPOSE 8090
ENV CONSUL_LOCATION consul

ADD ./target/streampipes-processors-text-mining-jvm.jar  /streampipes-processing-element-container.jar

ENTRYPOINT ["java", "-jar", "/streampipes-processing-element-container.jar"]
