FROM anapsix/alpine-java

EXPOSE 8090
ENV CONSUL_LOCATION consul

ADD ./target/streampipes-sinks-databases-flink.jar  /streampipes-sinks-databases-flink.jar

ENTRYPOINT ["java", "-jar", "/streampipes-sinks-databases-flink.jar"]
