FROM anapsix/alpine-java

ENV CONSUL_LOCATION consul

EXPOSE 8099

ADD target/streampipes-connect-container.jar  /streampipes-connect-container.jar

ENTRYPOINT ["java", "-jar", "/streampipes-connect-container.jar"]
