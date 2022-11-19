#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

# Dcokerfile for Apache RocketMQ 5.0.0 version
# Since the latest version 5.0.0 not in the official docker hub. We can build it locally.
# command: `docker build --tag apache_rocketmq .`
# Use openjdk not alpine due to the issue: https://github.com/grpc/grpc-java/issues/5369

FROM openjdk:8

ARG user=rocketmq
ARG group=rocketmq
ARG uid=3000
ARG gid=3000

# RocketMQ is run with user `rocketmq`, uid = 3000
# If you bind mount a volume from the host or a data container,
# ensure you use the same uid
RUN groupadd -g ${gid} ${group} \
    && useradd -u ${uid} -g ${gid} -m -s /bin/bash ${user}

ARG version=5.0.0

# Rocketmq version
ENV ROCKETMQ_VERSION ${version}

# Rocketmq home
ENV ROCKETMQ_HOME  /home/rocketmq/rocketmq-${ROCKETMQ_VERSION}

WORKDIR  ${ROCKETMQ_HOME}

# Install
RUN set -eux; \
    curl -L https://archive.apache.org/dist/rocketmq/${ROCKETMQ_VERSION}/rocketmq-all-${ROCKETMQ_VERSION}-bin-release.zip -o rocketmq.zip; \
    curl -L https://archive.apache.org/dist/rocketmq/${ROCKETMQ_VERSION}/rocketmq-all-${ROCKETMQ_VERSION}-bin-release.zip.asc -o rocketmq.zip.asc; \
	curl -L https://www.apache.org/dist/rocketmq/KEYS -o KEYS; \
	\
	gpg --import KEYS; \
    gpg --batch --verify rocketmq.zip.asc rocketmq.zip; \
    unzip rocketmq.zip; \
	mv rocketmq*/* . ; \
	rmdir rocketmq-* ; \
	rm rocketmq.zip rocketmq.zip.asc KEYS; \
    rm -rf /tmp/*

RUN chown -R ${uid}:${gid} ${ROCKETMQ_HOME}

# Expose namesrv port
EXPOSE 9876

# Expose broker ports
EXPOSE 10909 10911 10912

# Expose proxy port(grpc service port, the `rocketmq-client-java` endpoint config rely on this port)
EXPOSE 8081

RUN export JAVA_OPT=" -Duser.home=/home/rocketmq" \
 && sed -i 's/${JAVA_HOME}\/jre\/lib\/ext/${JAVA_HOME}\/jre\/lib\/ext:${JAVA_HOME}\/lib\/ext/' ${ROCKETMQ_HOME}/bin/tools.sh

USER ${user}

WORKDIR ${ROCKETMQ_HOME}/bin
