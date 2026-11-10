#!/bin/bash
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

# Starts the StreamPipes core once CouchDB, InfluxDB and NATS answer.

set -euo pipefail

wait_for() {
  local name=$1 url=$2
  until curl -fs -o /dev/null "$url"; do
    echo "[backend] waiting for $name ..."
    sleep 2
  done
}

wait_for CouchDB "http://localhost:5984/_up"
wait_for InfluxDB "http://localhost:8086/health"
wait_for NATS "http://localhost:8222/healthz"

echo "[backend] starting StreamPipes core"
cd /opt/streampipes/backend
# shellcheck disable=SC2086
exec java ${SP_BACKEND_JAVA_OPTS:--XX:MaxRAMPercentage=30} org.springframework.boot.loader.launch.JarLauncher
