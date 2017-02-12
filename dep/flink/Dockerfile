#FROM shouldbee/scala:2.11.7-openjdk8
FROM anapsix/alpine-java

ENV HADOOP_VERSION 2.7.0
ENV FLINK_VERSION 1.1.4
ENV SCALA_VERSION 2.10
ENV FLINK_ROOT_DIR /opt/flink

RUN apk update
RUN apk add curl
RUN apk add tar

# Get Hadoop from US Apache mirror and extract just the native
# libs. (Until we care about running HDFS with these containers, this
# is all we need.)
#RUN mkdir -p /opt && \
#    cd /opt && \
#    curl http://www.us.apache.org/dist/hadoop/common/hadoop-${HADOOP_VERSION}/hadoop-${HADOOP_VERSION}.tar.gz | \
#        tar -zx hadoop-${HADOOP_VERSION}/lib/native && \
#    ln -s hadoop-${HADOOP_VERSION} hadoop && \
#    echo Hadoop ${HADOOP_VERSION} native libraries installed in /opt/hadoop/lib/native

# Get Flink from US Apache mirror.
RUN mkdir -p /opt && \
    cd /opt && \
    curl http://www.us.apache.org/dist/flink/flink-${FLINK_VERSION}/flink-${FLINK_VERSION}-bin-hadoop27-scala_${SCALA_VERSION}.tgz | \
        tar -zx && \
    ln -s flink-${FLINK_VERSION} flink && \
    echo Flink ${FLINK_VERSION} installed in /opt


ADD log4j.properties logback.xml ${FLINK_ROOT_DIR}/conf/
ADD start-common.sh start-worker.sh start-master.sh ${FLINK_ROOT_DIR}/bin/
ADD flink-conf.yaml ${FLINK_ROOT_DIR}/conf/flink-conf.yaml

RUN addgroup -S flink
RUN adduser -D -S -H -G flink -h $FLINK_ROOT_DIR flink

RUN chown -R flink:flink /opt/flink

RUN chmod +x /opt/flink/bin/start-master.sh
RUN chmod +x /opt/flink/bin/start-common.sh
RUN chmod +x /opt/flink/bin/start-worker.sh

ENV PATH $PATH:${FLINK_ROOT_DIR}/bin

#ENTRYPOINT ["/opt/flink/bin/start-master.sh"]
CMD ["sh", "-c", "/opt/flink/bin/start-master.sh"]
