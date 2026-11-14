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

# Entrypoint of the all-in-one image: derives the credentials shared by the
# bundled services and hands over to supervisord.
#
# Secrets are generated once per container and kept in
# /var/lib/streampipes/secrets.env so that restarts of the same container keep
# working. Any SP_* variable passed with `docker run -e` wins over the
# generated value; this only has an effect on the first start.

set -euo pipefail

STATE_DIR=/var/lib/streampipes
SECRETS_FILE="$STATE_DIR/secrets.env"

random_secret() {
  python3 -c 'import secrets; print(secrets.token_hex(32))'
}

mkdir -p "$STATE_DIR"
if [ ! -f "$SECRETS_FILE" ]; then
  echo "[entrypoint] Generating service credentials for this container"
  umask 077
  cat > "$SECRETS_FILE" <<SECRETS
SP_GENERATED_SERVICE_SECRET=$(random_secret)
SP_GENERATED_JWT_SECRET=$(random_secret)
SP_GENERATED_COUCHDB_PASSWORD=$(random_secret)
SP_GENERATED_TS_STORAGE_TOKEN=$(random_secret)
SP_GENERATED_INFLUXDB_ADMIN_PASSWORD=$(random_secret)
SP_GENERATED_ENCRYPTION_PASSCODE=$(random_secret)
SP_GENERATED_NATS_TOKEN=$(random_secret)
SECRETS
  umask 022
fi

set -a
# shellcheck disable=SC1090
. "$SECRETS_FILE"
set +a

# Service account shared by core and extensions (SP_SERVICE_SECRET in the Compose installer)
export SP_INITIAL_SERVICE_USER_SECRET="${SP_INITIAL_SERVICE_USER_SECRET:-${SP_SERVICE_SECRET:-$SP_GENERATED_SERVICE_SECRET}}"
export SP_CLIENT_SECRET="${SP_CLIENT_SECRET:-$SP_INITIAL_SERVICE_USER_SECRET}"
export SP_JWT_SECRET="${SP_JWT_SECRET:-$SP_GENERATED_JWT_SECRET}"
export SP_ENCRYPTION_PASSCODE="${SP_ENCRYPTION_PASSCODE:-$SP_GENERATED_ENCRYPTION_PASSCODE}"

# NATS token authentication (same wiring as the Compose nats-auth override)
export SP_NATS_TOKEN="${SP_NATS_TOKEN:-$SP_GENERATED_NATS_TOKEN}"

# CouchDB
export SP_COUCHDB_PASSWORD="${SP_COUCHDB_PASSWORD:-$SP_GENERATED_COUCHDB_PASSWORD}"
export COUCHDB_PASSWORD="$SP_COUCHDB_PASSWORD"

# InfluxDB
export SP_TS_STORAGE_TOKEN="${SP_TS_STORAGE_TOKEN:-$SP_GENERATED_TS_STORAGE_TOKEN}"
export DOCKER_INFLUXDB_INIT_ADMIN_TOKEN="$SP_TS_STORAGE_TOKEN"
export DOCKER_INFLUXDB_INIT_PASSWORD="${SP_INFLUXDB_ADMIN_PASSWORD:-$SP_GENERATED_INFLUXDB_ADMIN_PASSWORD}"

# Initial StreamPipes administrator (documented demo credentials)
export SP_INITIAL_ADMIN_EMAIL="${SP_INITIAL_ADMIN_EMAIL:-admin@streampipes.apache.org}"
export SP_INITIAL_ADMIN_PASSWORD="${SP_INITIAL_ADMIN_PASSWORD:-admin}"

cat <<BANNER

**********************************************************************
  Apache StreamPipes all-in-one demo

  Starting CouchDB, NATS, InfluxDB, the StreamPipes core, the IIoT
  extensions and the UI in this container. The first start takes about
  a minute; the UI shows a progress screen until the core is ready.

  Open:      http://localhost (or the port you mapped to 80)
  Sign in:   $SP_INITIAL_ADMIN_EMAIL / $SP_INITIAL_ADMIN_PASSWORD
**********************************************************************

BANNER

exec /usr/bin/supervisord -n -c /etc/supervisor/supervisord.conf
