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

set -euo pipefail
cd "$(dirname "$0")"
export BACKEND_IMAGE="${1:-apachestreampipes/backend:0.98.0}"
target="${2:-streampipes_backend:release-validation}"
export EXTENSIONS_IMAGE="${3:-apachestreampipes/extensions-all-iiot:0.98.0}"
target_extensions="${4:-streampipes_pipeline-elements-all-iiot:release-validation}"
project="sp-upgrade-$(openssl rand -hex 8)"
SP_SERVICE_SECRET=$(openssl rand -hex 32)
export SP_SERVICE_SECRET

compose() { docker compose -f compose.yml -p "$project" "$@"; }
cleanup() {
  status=$?
  if (( status != 0 )); then compose logs --tail=100 backend extensions-all-iiot >&2 || true; fi
  compose down --volumes || exit 1
  exit "$status"
}
trap cleanup EXIT
trap 'exit 130' INT
trap 'exit 143' TERM

login() {
  base="http://$(compose port backend 8030)/streampipes-backend/api"
  for ((attempt=0; attempt<120; attempt++)); do
    if token=$(curl -fsS --max-time 5 -H 'Content-Type: application/json' \
      -d '{"username":"admin@streampipes.apache.org","password":"admin"}' \
      "$base/v2/auth/login" 2>/dev/null | jq -er '.accessToken | strings | select(length > 0)'); then
      return
    fi
    sleep 2
  done
  echo "Timed out waiting for login on $BACKEND_IMAGE" >&2
  return 1
}
api() {
  local path=$1
  shift
  curl -fsS --max-time 20 -H "Authorization: Bearer $token" \
    -H 'Content-Type: application/json' "$base/$path" "$@"
}
dashboard=v3/datalake/dashboard
links=v2/storage-generic/asset-link-type
echo "Starting source $BACKEND_IMAGE"
compose up -d
login
id=$(api "$dashboard" -d '{"name":"Release upgrade test","widgets":[]}' | jq -er '.elementId | strings | select(length > 0)')
check_dashboard() {
  api "$dashboard/$id" | jq -e --arg id "$id" \
    '.elementId == $id and .name == "Release upgrade test"' >/dev/null
}
check_dashboard
echo "Checking the source measurement link type"
legacy_id=$(api "$links" | jq -er 'first(.[] | select(.linkType == "measurement") | ._id | strings | select(length > 0))')
# 0.98 creates and starts the persist pipeline when saving this compact adapter.
adapters=v2/connect/master/adapters
pipelines=v2/pipelines
measurement=v4/datalake/measurements/upgrade_pressure
wait_for() {
  local description=$1 deadline=$((SECONDS + 180))
  shift
  until "$@"; do
    if (( SECONDS >= deadline )); then
      echo "Timed out waiting for $description" >&2
      return 1
    fi
    sleep 2
  done
}
extensions_installed() {
  api v2/extension-items | jq -e '
    [.[] | select(.installed) | .appId] | contains([
      "org.apache.streampipes.connect.iiot.adapters.simulator.machine",
      "org.apache.streampipes.sinks.internal.jvm.datalake"])' >/dev/null
}
wait_for "source simulator and data lake sink installation" extensions_installed
api v2/connect/compact-adapters --data-binary @pressure-adapter.json | jq -e '.success == true'
running_id() {
  api "$1" | jq -er --arg name "$2" 'first(.[] | select(.name == $name and .running) | .elementId | strings)'
}
capture_ids() {
  adapter_id=$(running_id "$adapters" upgrade_pressure) &&
    pipeline_id=$(running_id "$pipelines" 'Persist upgrade_pressure')
}
wait_for "source adapter and persist pipeline" capture_ids
# Query only time and pressure so rows are comparable across both releases.
rows() {
  api "$measurement?columns=pressure&order=ASC&limit=10&$1" |
    jq -ce '[.allDataSeries[].rows[] | select((.[0] | type) == "number" and (.[1] | type) == "number")]'
}
capture_history() {
  history=$(rows "" | jq -ce 'unique_by(.[0]) | select(length >= 2) | .[:2]')
}
wait_for "two persisted source pressure samples" capture_history
first=$(jq -r 'first[0]' <<< "$history")
last=$(jq -r 'last[0]' <<< "$history")
echo "Captured source pressure samples: $history"
echo "Upgrading backend to $target and extensions to $target_extensions"
export BACKEND_IMAGE="$target"
export EXTENSIONS_IMAGE="$target_extensions"
# Allow the source backend to stop its workloads while the worker is reachable.
compose stop backend
compose stop extensions-all-iiot
compose up -d --no-deps --force-recreate backend extensions-all-iiot
check_upgrade() {
  login
  echo "Checking migrated dashboard and dataset link"
  check_dashboard
  api "$links" | jq -e --arg id "$legacy_id" \
    'any(.[]; ._id == $id and .linkType == "dataset" and .linkQueryHint == "dataset")' >/dev/null
  local from=$(( ($(date +%s) + 1) * 1000 ))
  local historical_rows
  historical_rows=$(rows "startDate=$((first-1))&endDate=$((last+1))")
  echo "Historical pressure samples from candidate: $historical_rows"
  jq -en --argjson expected "$history" --argjson actual "$historical_rows" \
    '$expected == $actual' >/dev/null || {
      echo "Historical samples differ from the source" >&2
      return 1
    }
  wait_for "automatic recovery of the original adapter and pipeline" recovered
  wait_for "two new pressure samples after startup" fresh_samples "$from"
  logs=$(compose logs --no-color backend)
  if [[ "$logs" == *"An error has occurred while executing migration"* ]]; then
    echo "Backend reported a migration failure" >&2
    return 1
  fi
}
recovered() {
  [[ $(running_id "$adapters" upgrade_pressure) == "$adapter_id" &&
     $(running_id "$pipelines" 'Persist upgrade_pressure') == "$pipeline_id" ]]
}
fresh_samples() {
  rows "startDate=$1" | jq -ce --argjson from "$1" \
    'map(select(.[0] >= $from)) | unique_by(.[0]) | select(length >= 2) | .[:2]'
}
check_upgrade
compose restart backend
check_upgrade
echo "Upgrade and restart passed"
