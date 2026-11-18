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

# Starts the IIoT extensions service once the core accepts HTTP connections.
# Registration with the core is retried by the service itself, so it does not
# have to wait for the core to finish its initial setup.

set -euo pipefail

until curl -s -o /dev/null "http://localhost:8030/streampipes-backend/api/v2/setup/configured"; do
  echo "[extensions] waiting for the StreamPipes core ..."
  sleep 2
done

echo "[extensions] starting extensions service"
cd /opt/streampipes/extensions
# shellcheck disable=SC2086
exec java ${SP_EXTENSIONS_JAVA_OPTS:--XX:MaxRAMPercentage=30} org.springframework.boot.loader.launch.JarLauncher
