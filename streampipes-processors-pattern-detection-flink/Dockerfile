FROM anapsix/alpine-java

EXPOSE 8090
ENV CONSUL_LOCATION consul

ADD ./target/streampipes-processors-pattern-detection-flink.jar  /streampipes-processors-pattern-detection-flink.jar

ENTRYPOINT ["java", "-jar", "/streampipes-processors-pattern-detection-flink.jar"]
