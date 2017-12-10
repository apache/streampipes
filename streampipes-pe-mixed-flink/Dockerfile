FROM anapsix/alpine-java


ENV CONSUL_LOCATION consul

EXPOSE 8090
RUN echo 'hosts: files mdns4_minimal [NOTFOUND=return] dns mdns4' >> /etc/nsswitch.conf

ADD target/streampipes-pe-mixed-flink.jar  /streampipes-pe-mixed-flink.jar

ENTRYPOINT ["java", "-jar", "/streampipes-pe-mixed-flink.jar"]
