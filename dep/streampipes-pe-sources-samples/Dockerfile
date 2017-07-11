FROM anapsix/alpine-java

EXPOSE 8089
EXPOSE 61616
RUN echo 'hosts: files mdns4_minimal [NOTFOUND=return] dns mdns4' >> /etc/nsswitch.conf

ADD ./org.streampipes/streampipes-pe-sources-samples/0.41.0-SNAPSHOT/streampipes-pe-sources-samples-0.41.0-SNAPSHOT.jar  /streampipes-pe-sources-samples.jar

ENTRYPOINT ["java", "-jar", "/streampipes-pe-sources-samples.jar"]
