FROM anapsix/alpine-java


ENV CONSUL_LOCATION consul

EXPOSE 8090

RUN echo 'hosts: files mdns4_minimal [NOTFOUND=return] dns mdns4' >> /etc/nsswitch.conf

ADD target/streampipes-pe-processors-esper.jar /streampipes-pe-processors-esper.jar

ENTRYPOINT ["java", "-Xms2G", "-Xmx4G", "-jar", "/streampipes-pe-processors-esper.jar"]
