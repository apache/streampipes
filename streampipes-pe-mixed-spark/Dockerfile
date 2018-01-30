FROM anapsix/alpine-java


ENV CONSUL_LOCATION consul

EXPOSE 8090
RUN echo 'hosts: files mdns4_minimal [NOTFOUND=return] dns mdns4' >> /etc/nsswitch.conf

RUN wget http://apache.mirror.iphh.net/spark/spark-2.1.2/spark-2.1.2-bin-hadoop2.7.tgz \
 && cd /opt \
 && tar xfz /spark-2.1.2-bin-hadoop2.7.tgz \
 && rm /spark-2.1.2-bin-hadoop2.7.tgz
ENV SPARK_HOME /opt/spark-2.1.2-bin-hadoop2.7

ADD target/streampipes-pe-mixed-spark.jar  /streampipes-pe-mixed-spark.jar

ENTRYPOINT ["java", "-jar", "/streampipes-pe-mixed-spark.jar"]
