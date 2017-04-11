FROM guoguojin/ubuntu-scala
#FROM denvazh/scala:2.10.5-openjdk8

ARG HADOOP_VERSION=26
ARG FLINK_VERSION=1.2.0
ARG SCALA_BINARY_VERSION=2.11

ENV FLINK_INSTALL_PATH /opt
ENV FLINK_HOME $FLINK_INSTALL_PATH/flink
ENV PATH $PATH:$FLINK_HOME/bin

RUN apt-get install curl
RUN set -x && \
    mkdir -p ${FLINK_INSTALL_PATH} && \
    #apk --update add --virtual curl && \
    curl -s https://archive.apache.org/dist/flink/flink-${FLINK_VERSION}/flink-${FLINK_VERSION}-bin-hadoop${HADOOP_VERSION}-scala_${SCALA_BINARY_VERSION}.tgz | \
    tar xz -C ${FLINK_INSTALL_PATH} && \
    ln -s ${FLINK_INSTALL_PATH}/flink-${FLINK_VERSION} ${FLINK_HOME} && \
    sed -i -e "s/echo \$mypid >> \$pid/echo \$mypid >> \$pid \&\& wait/g" ${FLINK_HOME}/bin/flink-daemon.sh && \
    sed -i -e "s/ > \"\$out\" 2>&1 < \/dev\/null//g" ${FLINK_HOME}/bin/flink-daemon.sh && \
    rm -rf /var/cache/apk/* && \
    echo Installed Flink ${FLINK_VERSION} to ${FLINK_HOME}

#RUN apk update && apk add netcat-openbsd
RUN apt-get update && apt-get install netcat-openbsd

WORKDIR ${FLINK_HOME}/lib

#include nd4j native libraries into flink image to avoid classloader issues
RUN curl -u external:streampipes2017 https://laus.fzi.de/nexus/content/repositories/snapshots/de/fzi/nd4j-platform-fatjar/1.0-SNAPSHOT/nd4j-platform-fatjar-1.0-20170411.204457-1.jar
ADD docker-entrypoint.sh ${FLINK_HOME}/bin/

# Additional output to console, allows gettings logs with 'docker-compose logs'

ADD log4j.properties ${FLINK_HOME}/conf/
WORKDIR ${FLINK_HOME}/bin
RUN chmod +x docker-entrypoint.sh

ENTRYPOINT ["docker-entrypoint.sh"]
CMD ["sh", "-c"]