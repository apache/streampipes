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

# Apache StreamPipes all-in-one demo image.
#
# Bundles CouchDB, NATS, InfluxDB, the StreamPipes core, the IIoT extensions
# service and the UI (nginx) into a single container supervised by
# supervisord. The StreamPipes parts come from the build artifacts of this
# repository, handed in as named build contexts (see build.sh):
#
#   core        -> streampipes-service-core/target (streampipes-core.jar)
#   extensions  -> streampipes-extensions/streampipes-extensions-all-iiot/target
#                  (streampipes-extensions-all-iiot.jar)
#   ui          -> ui/dist/streampipes/ui/browser
#
# The third-party services use the same images as installer/compose.
# Only port 80 is exposed.

ARG COUCHDB_VERSION=3.4.3
ARG INFLUXDB_VERSION=2.6
ARG NATS_VERSION=2.11

FROM eclipse-temurin:25-jre-jammy AS jre

# Explode the Spring Boot jars into layers, as the component images do
FROM eclipse-temurin:25-jre-jammy AS backend-layers
WORKDIR /layers
COPY --from=core streampipes-core.jar application.jar
RUN java -Djarmode=layertools -jar application.jar extract && rm application.jar

FROM eclipse-temurin:25-jre-jammy AS extensions-layers
WORKDIR /layers
COPY --from=extensions streampipes-extensions-all-iiot.jar application.jar
RUN java -Djarmode=layertools -jar application.jar extract && rm application.jar
FROM influxdb:${INFLUXDB_VERSION} AS influxdb
FROM nats:${NATS_VERSION} AS nats

# CouchDB is the only service that cannot be copied as a static binary, so its
# image (Debian) is the base; everything else is layered on top of it. 3.4.x is
# the first CouchDB image on a Debian release that still receives updates
# (the Compose installer's 3.3.1 is built on end-of-life bullseye).
FROM couchdb:${COUCHDB_VERSION}

RUN apt-get update \
    && apt-get install -y --no-install-recommends nginx-light supervisor python3 \
    && rm -rf /var/lib/apt/lists/* /etc/nginx/sites-enabled/default \
    && useradd --system --user-group --home-dir /var/lib/influxdb2 --shell /usr/sbin/nologin influxdb \
    && mkdir -p /var/lib/influxdb2 /etc/influxdb2 /var/lib/streampipes /opt/streampipes/bin \
    && chown influxdb:influxdb /var/lib/influxdb2 /etc/influxdb2

# Java runtime (Temurin builds run on this base's glibc)
COPY --from=jre /opt/java/openjdk /opt/java/openjdk

# NATS
COPY --from=nats /nats-server /usr/local/bin/nats-server

# InfluxDB, including the upstream entrypoint that performs the initial setup
COPY --from=influxdb /usr/local/bin/influxd /usr/local/bin/influx /usr/local/bin/gosu /usr/local/bin/yq /usr/local/bin/
COPY --from=influxdb /entrypoint.sh /opt/streampipes/bin/influxdb-entrypoint.sh
COPY --from=influxdb /etc/defaults/influxdb2/config.yml /etc/defaults/influxdb2/config.yml

# StreamPipes core and extensions (exploded Spring Boot layers)
COPY --from=backend-layers /layers/dependencies/ /opt/streampipes/backend/
COPY --from=backend-layers /layers/snapshot-dependencies/ /opt/streampipes/backend/
COPY --from=backend-layers /layers/spring-boot-loader/ /opt/streampipes/backend/
COPY --from=backend-layers /layers/application/ /opt/streampipes/backend/
COPY --from=extensions-layers /layers/dependencies/ /opt/streampipes/extensions/
COPY --from=extensions-layers /layers/snapshot-dependencies/ /opt/streampipes/extensions/
COPY --from=extensions-layers /layers/spring-boot-loader/ /opt/streampipes/extensions/
COPY --from=extensions-layers /layers/application/ /opt/streampipes/extensions/

# StreamPipes UI (production build of ui/)
COPY --from=ui / /usr/share/nginx/html/

COPY supervisord.conf /etc/supervisor/supervisord.conf
COPY nginx/streampipes.conf /etc/nginx/conf.d/streampipes.conf
COPY bin/ /opt/streampipes/bin/
RUN chmod 755 /opt/streampipes/bin/*

ENV JAVA_HOME=/opt/java/openjdk \
    PATH=/opt/java/openjdk/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin \
    INFLUXD_INIT_PORT=9999 \
    INFLUXD_INIT_PING_ATTEMPTS=600 \
    INFLUX_CONFIGS_PATH=/etc/influxdb2/influx-configs \
    INFLUXD_NATS_PORT=4223 \
    INFLUXD_REPORTING_DISABLED=true \
    DOCKER_INFLUXDB_INIT_CLI_CONFIG_NAME=default \
    DOCKER_INFLUXDB_INIT_MODE=setup \
    DOCKER_INFLUXDB_INIT_USERNAME=admin \
    DOCKER_INFLUXDB_INIT_ORG=sp \
    DOCKER_INFLUXDB_INIT_BUCKET=sp \
    COUCHDB_USER=admin \
    SP_HOST=localhost \
    SP_CORE_HOST=localhost \
    SP_CORE_PORT=8030 \
    SP_COUCHDB_HOST=localhost \
    SP_COUCHDB_USER=admin \
    SP_TS_STORAGE_HOST=localhost \
    SP_TS_STORAGE_ORG=sp \
    SP_TS_STORAGE_BUCKET=sp \
    SP_NATS_HOST=localhost \
    SP_NATS_PORT=4222 \
    SP_PRIORITIZED_PROTOCOL=nats \
    SP_DEMO_SEED=true

EXPOSE 80

HEALTHCHECK --interval=10s --timeout=5s --start-period=180s --retries=3 \
    CMD curl -fs http://localhost/streampipes-backend/api/v2/setup/configured | grep -q '"configured":true' || exit 1

ENTRYPOINT ["tini", "--", "/opt/streampipes/bin/entrypoint.sh"]
