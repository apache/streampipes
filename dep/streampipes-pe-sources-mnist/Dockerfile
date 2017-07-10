FROM anapsix/alpine-java

EXPOSE 8088
EXPOSE 61616

ADD ./org.streampipes/streampipes-pe-sources-mnist/0.40.3-SNAPSHOT/streampipes-pe-sources-mnist-0.40.3-SNAPSHOT.jar  /streampipes-pe-sources-mnist.jar

ENTRYPOINT ["java", "-jar", "/streampipes-pe-sources-mnist.jar"]
