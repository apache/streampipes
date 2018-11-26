FROM anapsix/alpine-java

EXPOSE 8090
ENV CONSUL_LOCATION consul

ADD ./target/streampipes-sinks-internal-jvm.jar  /streampipes-processing-element-container.jar

ENTRYPOINT ["java", "-jar", "/streampipes-processing-element-container.jar"]
