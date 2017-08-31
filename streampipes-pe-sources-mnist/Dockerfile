FROM anapsix/alpine-java


ENV CONSUL_LOCATION consul

EXPOSE 8090
EXPOSE 61616

ADD target/streampipes-pe-sources-mnist-0.41.0-SNAPSHOT.jar  /streampipes-pe-sources-mnist.jar

ENTRYPOINT ["java", "-jar", "/streampipes-pe-sources-mnist.jar"]
