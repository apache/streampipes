FROM anapsix/alpine-java

EXPOSE 8090
ENV CONSUL_LOCATION consul

ADD ./target/streampipes-sources-watertank-simulator.jar  /streampipes-sources-watertank-simulator.jar

ENTRYPOINT ["java", "-jar", "/streampipes-sources-watertank-simulator.jar"]
