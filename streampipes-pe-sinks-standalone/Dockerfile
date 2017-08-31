FROM anapsix/alpine-java


ENV CONSUL_LOCATION consul

EXPOSE 8090

RUN echo 'hosts: files mdns4_minimal [NOTFOUND=return] dns mdns4' >> /etc/nsswitch.conf

ADD target/streampipes-pe-sinks-standalone-0.41.2-SNAPSHOT.jar  /streampipes-pe-sinks-standalone.jar

ENTRYPOINT ["java", "-jar", "/streampipes-pe-sinks-standalone.jar"]
