FROM anapsix/alpine-java

EXPOSE 8088
EXPOSE 61616

ADD ./org.streampipes/streampipes-pe-sources-mnist/0.41.0-SNAPSHOT/streampipes-pe-sources-mnist-0.41.0-SNAPSHOT.jar  /streampipes-pe-sources-mnist.jar

ENTRYPOINT ["java", "-jar", "/streampipes-pe-sources-mnist.jar"]
