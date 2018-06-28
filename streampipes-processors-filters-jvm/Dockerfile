FROM anapsix/alpine-java

EXPOSE 8090
ENV CONSUL_LOCATION consul

ADD ./target/streampipes-processors-filters-jvm.jar  /streampipes-processors-filters-jvm.jar

ENTRYPOINT ["java", "-jar", "/streampipes-processors-filters-jvm.jar"]
