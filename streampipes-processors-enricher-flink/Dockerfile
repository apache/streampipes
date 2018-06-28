FROM anapsix/alpine-java

EXPOSE 8090
ENV CONSUL_LOCATION consul

ADD ./target/streampipes-processors-enricher-flink.jar  /streampipes-processors-enricher-flink.jar

ENTRYPOINT ["java", "-jar", "/streampipes-processors-enricher-flink.jar"]
