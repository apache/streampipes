FROM anapsix/alpine-java

EXPOSE 8070

ADD ./org.streampipes/streampipes-pe-sources-axoom-hmi/0.41.0-SNAPSHOT/streampipes-pe-sources-axoom-hmi-0.41.0-SNAPSHOT.jar  /streampipes-pe-sources-axoom-hmi.jar

ENTRYPOINT ["java", "-jar", "/streampipes-pe-sources-axoom-hmi.jar"]
