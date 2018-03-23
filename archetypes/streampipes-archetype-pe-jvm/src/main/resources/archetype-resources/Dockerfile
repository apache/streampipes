FROM anapsix/alpine-java

ENV CONSUL_LOCATION consul

EXPOSE 8090

RUN echo 'hosts: files mdns4_minimal [NOTFOUND=return] dns mdns4' >> /etc/nsswitch.conf

ADD target/${artifactId}.jar  /${artifactId}.jar

ENTRYPOINT ["java", "-jar", "/${artifactId}.jar"]
