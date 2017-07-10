FROM anapsix/alpine-java

EXPOSE 8089
EXPOSE 61616
RUN echo 'hosts: files mdns4_minimal [NOTFOUND=return] dns mdns4' >> /etc/nsswitch.conf

ADD ./org.streampipes/streampipes-pe-sources-sample/0.40.3-SNAPSHOT/streampipes-pe-sources-sample-0.40.3-SNAPSHOT.jar  /streampipes-pe-sources-sample.jar

ENTRYPOINT ["java", "-jar", "/streampipes-pe-sources-sample.jar"]
