FROM anapsix/alpine-java

EXPOSE 8090
ENV CONSUL_LOCATION consul

ADD ./target/streampipes-sinks-databases-jvm.jar  /streampipes-sinks-databases-jvm.jar

ENTRYPOINT ["java", "-jar", "/streampipes-sinks-databases-jvm.jar"]
