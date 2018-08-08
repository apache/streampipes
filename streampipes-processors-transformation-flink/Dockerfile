FROM anapsix/alpine-java

EXPOSE 8090
ENV CONSUL_LOCATION consul

ADD ./target/streampipes-processors-transformation-flink.jar  /streampipes-processors-transformation-flink.jar

ENTRYPOINT ["java", "-jar", "/streampipes-processors-transformation-flink.jar"]
