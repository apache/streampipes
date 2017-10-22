FROM anapsix/alpine-java


ENV CONSUL_LOCATION consul

EXPOSE 8090
EXPOSE 61616
RUN echo 'hosts: files mdns4_minimal [NOTFOUND=return] dns mdns4' >> /etc/nsswitch.conf

ADD target/streampipes-pe-sources-samples.jar  /streampipes-pe-sources-samples.jar

ENTRYPOINT ["java", "-jar", "/streampipes-pe-sources-samples.jar"]
