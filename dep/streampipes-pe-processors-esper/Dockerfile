FROM anapsix/alpine-java
EXPOSE 8090

RUN echo 'hosts: files mdns4_minimal [NOTFOUND=return] dns mdns4' >> /etc/nsswitch.conf

ADD ./org.streampipes/streampipes-pe-processors-esper/0.40.3-SNAPSHOT/streampipes-pe-processors-esper-0.40.3-SNAPSHOT.jar /streampipes-pe-processors-esper.jar

ENTRYPOINT ["java", "-Xms2G", "-Xmx4G", "-jar", "/streampipes-pe-processors-esper.jar"]
