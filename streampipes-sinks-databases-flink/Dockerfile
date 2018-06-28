FROM anapsix/alpine-java

EXPOSE 8090
ENV CONSUL_LOCATION consul

ADD ./target/streampipes-examples-sources.jar  /streampipes-examples-sources.jar

ENTRYPOINT ["java", "-jar", "/streampipes-examples-sources.jar"]
