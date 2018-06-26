FROM anapsix/alpine-java

EXPOSE 8090
ENV CONSUL_LOCATION consul

ADD ./target/streampipes-examples-jvm.jar  /streampipes-examples-jvm.jar

ENTRYPOINT ["java", "-jar", "/streampipes-examples-jvm.jar"]
