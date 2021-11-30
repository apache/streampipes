# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

ARG BASE_IMAGE=arm64v8/openjdk:11-jre-slim

FROM arm64v8/ubuntu:18.04 as build-dev
RUN apt -y update; \
    apt -y --no-install-recommends install qemu-user-static

FROM $BASE_IMAGE
MAINTAINER dev@streampipes.apache.org

EXPOSE 7077 9010
ENV CONSUL_LOCATION consul

COPY --from=build-dev /usr/bin/qemu-aarch64-static /usr/bin
RUN set -ex; \
    apt -y update; \
    apt -y --no-install-recommends install curl; \
    apt clean; \
    rm -rf /tmp/apache-* /var/lib/apt/lists/*

COPY target/streampipes-node-controller.jar  /streampipes-node-controller.jar

ENTRYPOINT ["java", \
            "-Djava.rmi.server.hostname=0.0.0.0", \
            "-Dcom.sun.management.jmxremote", \
            "-Dcom.sun.management.jmxremote.port=9010", \
            "-Dcom.sun.management.jmxremote.rmi.port=9010", \
            "-Dcom.sun.management.jmxremote.local.only=false", \
            "-Dcom.sun.management.jmxremote.ssl=false", \
            "-Dcom.sun.management.jmxremote.authenticate=false", \
            "-jar", \
            "/streampipes-node-controller.jar"]
