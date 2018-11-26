FROM anapsix/alpine-java

EXPOSE 8090
ENV CONSUL_LOCATION consul

ADD ./target/streampipes-processors-image-processing-jvm.jar  /streampipes-processing-element-container.jar

ENTRYPOINT ["java", "-jar", "/streampipes-processing-element-container.jar"]
