FROM anapsix/alpine-java

EXPOSE 8094
RUN echo 'hosts: files mdns4_minimal [NOTFOUND=return] dns mdns4' >> /etc/nsswitch.conf

ADD ./org.streampipes/streampipes-pe-mixed-flink/0.41.0-SNAPSHOT/streampipes-pe-mixed-flink-0.41.0-SNAPSHOT.jar  /streampipes-pe-mixed-flink.jar

ENTRYPOINT ["java", "-jar", "/streampipes-pe-mixed-flink.jar"]
