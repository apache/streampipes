#!/usr/bin/env bash
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
if [[ $(uname -s) != Linux || ! -r /proc/$$/stat ]]; then
  echo "Run this script in the Linux Dev Container terminal. It needs /proc to track service processes." >&2
  exit 1
fi
cd "$(dirname -- "$0")/../.."
repo_dir=$PWD
script="$repo_dir/.devcontainer/scripts/services.sh"
core_module=streampipes-service-core
ext_module=streampipes-extensions/streampipes-extensions-all-iiot
declare -A jar=([core]="$core_module/target/streampipes-core.jar" [extensions]="$ext_module/target/streampipes-extensions-all-iiot.jar")
declare -A port=([core]=8030 [extensions]=8090 [ui]=8082)
declare -A started=()
state_dir="${XDG_CACHE_HOME:-$HOME/.cache}/streampipes/dev-$(printf '%s' "$repo_dir" | sha256sum | cut -c1-16)"
mkdir -p -m 700 "$state_dir"

run_service() {
  case "$1" in
    core|extensions)
      export SP_INITIAL_SERVICE_USER_SECRET="${SP_INITIAL_SERVICE_USER_SECRET:-streampipes-development-only-service-secret}"
      export SP_CLIENT_SECRET="${SP_CLIENT_SECRET:-$SP_INITIAL_SERVICE_USER_SECRET}"
      exec java -jar "${jar[$1]}"
      ;;
    ui)
      cd ui
      # The persistent node_modules volume can outlive a branch or lockfile change.
      local lock_hash
      lock_hash=$(sha256sum package-lock.json)
      if [[ ! -x node_modules/.bin/ng || $(cat node_modules/.streampipes-lock 2>/dev/null) != "$lock_hash" ]]; then
        npm ci --no-audit --no-fund
        printf '%s\n' "$lock_hash" > node_modules/.streampipes-lock
      fi
      node deployment/prebuild.js
      # The app's tsconfig resolves both local libraries to their source entry points.
      exec node_modules/.bin/ng serve --host 0.0.0.0 --port 8082 --proxy-config src/proxy.conf.json
      ;;
  esac
}

# Linux /proc/stat fields after the parenthesized command (which can contain spaces).
identity() {
  local stat
  local -a fields
  stat=$(cat "/proc/$1/stat" 2>/dev/null) || return 1
  read -ra fields <<< "${stat##*) }"
  [[ ${fields[0]} != Z && ${fields[2]} == "$1" && ${fields[3]} == "$1" ]] || return 1
  printf '%s\n' "${fields[19]}"
}

managed() {
  local stamp
  pid=
  running_version=
  [[ -f "$state_dir/$1.pid" ]] && read -r pid stamp running_version < "$state_dir/$1.pid" &&
    [[ $pid =~ ^[1-9][0-9]*$ && $stamp =~ ^[0-9]+$ && $(identity "$pid") == "$stamp" ]]
}

jar_version() {
  unzip -p "${2:-$repo_dir}/${jar[$1]}" META-INF/MANIFEST.MF 2>/dev/null | awk '
    /^Implementation-Version: / { version = $2; sub(/\r$/, "", version) }
    /^Start-Class: / { boot = 1 }
    END { if (boot && version) print version; else exit 1 }'
}

# Runs in the launcher's cancellable process group. Only a 9p checkout needs
# a Linux build directory. Keep the published JAR paths the same in both cases.
package_backend() (
  local version=$1 service artifact
  shift
  local -a projects=()
  for service in "$@"; do
    case "$service" in
      core) projects+=("$core_module") ;;
      extensions) projects+=("$ext_module") ;;
      *) return 2 ;;
    esac
  done
  local project_list
  project_list=$(IFS=,; echo "${projects[*]}")
  local -a command=(mvn -B -ntp -pl "$project_list" -am -Dmaven.test.skip=true
                    -Dmaven.javadoc.skip=true -Dsbom.skip=true -Dassembly.skipAssembly=true package)
  if [[ $(findmnt -n -o FSTYPE -T "$repo_dir") != 9p ]]; then
    exec "${command[@]}"
  fi

  stage_dir=
  snapshot=$(mktemp -d /tmp/streampipes-build.XXXXXXXX)
  trap '[[ -z $stage_dir ]] || rm -rf -- "$stage_dir"; rm -rf -- "$snapshot"' EXIT
  trap 'exit 143' TERM
  trap 'exit 130' INT
  echo "Copying backend sources from the 9p checkout to $snapshot"
  local copy_started=$SECONDS
  tar --exclude='./.git' --exclude='./.idea' --exclude='./.devcontainer' --exclude='./ui' \
      --exclude='./.mvn/.develocity' --exclude='*/target' --exclude=node_modules --exclude=.venv \
      -cf - . | tar -C "$snapshot" --no-same-owner -xf -
  echo "Backend sources copied in $((SECONDS - copy_started))s"
  (cd "$snapshot" && exec "${command[@]}")

  for service in "$@"; do
    if [[ $(jar_version "$service" "$snapshot") != "$version" ]]; then
      echo "Maven did not produce the expected $version JAR for $service." >&2
      return 1
    fi
  done
  for service in "$@"; do
    artifact=${jar[$service]}
    mkdir -p -- "${artifact%/*}"
    stage_dir=$(mktemp -d "${artifact%/*}/.streampipes-build.XXXXXXXX")
    cp -- "$snapshot/$artifact" "$stage_dir/package.jar"
    mv -fT -- "$stage_dir/package.jar" "$artifact"
    rmdir -- "$stage_dir"
    stage_dir=
  done
)

group_alive() {
  ps -eo pgid=,stat= | awk -v g="$1" '$1 == g && $2 !~ /^Z/ {alive=1} END {exit !alive}'
}

occupied() { [[ -n $(ss -H -ltn "sport = :${port[$1]}") ]]; }

status() {
  if managed "$1"; then echo "$1 running (PID $pid)"
  elif occupied "$1"; then echo "$1 port ${port[$1]} is in use outside this launcher"
  else echo "$1 stopped"; fi
}

stop_service() {
  local service=$1 group i
  if ! managed "$service"; then
    if occupied "$service" || { [[ $pid =~ ^[1-9][0-9]*$ ]] && group_alive "$pid"; }; then
      echo "Cannot stop $service safely: check port ${port[$service]} and the recorded process group ${pid:-unknown}." >&2
      return 1
    fi
    rm -f -- "$state_dir/$service.pid"
    echo "$service stopped"
    return 0
  fi
  group=$pid
  echo "Stopping $service"
  kill -TERM -- "-$group" || return 1
  for ((i=0; i<60; i++)); do
    if ! group_alive "$group"; then
      rm -f -- "$state_dir/$service.pid"
      return 0
    fi
    sleep 1
  done
  echo "$service did not shut down within 60 seconds. See $state_dir/$service.log" >&2
  return 1
}

start_service() {
  local service=$1 child stamp i
  if managed "$service"; then status "$service"; return; fi
  echo "Starting $service (log: $state_dir/$service.log)"
  nohup setsid bash "$script" run "$service" 9>&- </dev/null >"$state_dir/$service.log" 2>&1 &
  child=$!
  for ((i=0; i<50; i++)); do
    if stamp=$(identity "$child"); then
      printf '%s %s %s\n' "$child" "$stamp" "${project_version:-}" > "$state_dir/$service.pid"
      started[$service]=1
      return
    fi
    kill -0 "$child" 2>/dev/null || break
    sleep 0.1
  done
  tail -30 "$state_dir/$service.log" >&2
  return 1
}

ready() {
  case "$1" in
    # The setup endpoint returns a single boolean field when the core is ready.
    core) curl --noproxy '*' -fsS --max-time 3 http://127.0.0.1:8030/streampipes-backend/api/v2/setup/configured 2>/dev/null |
            grep -zEq '^[[:space:]]*\{[[:space:]]*"configured"[[:space:]]*:[[:space:]]*true[[:space:]]*\}[[:space:]]*$' ;;
    extensions) curl --noproxy '*' -fsS --max-time 3 http://127.0.0.1:8090/health 2>/dev/null |
                  grep -F '"adapterInstanceStates"' >/dev/null ;;
    installation) grep -Eq 'Found [1-9][0-9]* extensions which we will install' "$state_dir/core.log" &&
                    grep -Fq 'Initial setup completed successfully' "$state_dir/core.log" ;;
    ui)
      local index module url=http://127.0.0.1:8082
      index=$(curl --noproxy '*' -fsS --max-time 3 -w '\n%{http_code}' "$url/" 2>/dev/null) || return 1
      [[ $index == *$'\n200' && $index == *'<sp-app-root>'* && $index == *'src="main.js"'* ]] || return 1
      module=$(curl --noproxy '*' -fsSI --max-time 3 -o /dev/null -w '%{http_code} %{content_type}' "$url/main.js" 2>/dev/null) || return 1
      [[ ${module%%;*} == '200 text/javascript' || ${module%%;*} == '200 application/javascript' ]] ;;
  esac
}

wait_ready() {
  local service=$1 check=${2:-$1} deadline=$((SECONDS + 1800))
  until managed "$service" && ready "$check"; do
    check_stop_request "${services[@]}" || return $?
    if [[ $check == installation ]] && grep -Fq 'Found 0 extensions which we will install' "$state_dir/core.log"; then
      echo "Core found no pipeline elements during initial setup. If the catalog is still empty, open 'Install pipeline elements' in the UI." >&2
      return 0
    fi
    if [[ $check == installation ]] && grep -Fq 'errors occurred during the setup process' "$state_dir/core.log"; then
      echo "Initial pipeline element installation failed. See $state_dir/core.log" >&2
      return 1
    fi
    if ! managed "$service" || ((SECONDS >= deadline)); then
      echo "$service failed to become ready:" >&2
      tail -40 "$state_dir/$service.log" >&2
      return 1
    fi
    sleep 2
  done
  echo "$check ready"
}

usage() {
  echo "Usage: services.sh start [all|core|extensions|ui] [--build] | {stop|status} [all|core|extensions|ui]" >&2
  exit 2
}

if [[ ${1:-} == package-backend ]]; then
  (( $# >= 3 )) || usage
  shift
  package_backend "$@"
  exit
fi
(( $# )) || usage
action=$1
shift
selection=
build=false
for arg in "$@"; do
  case "$arg" in
    --build)
      [[ $action == start && $build == false ]] || usage
      build=true ;;
    all|core|extensions|ui)
      [[ -z $selection ]] || usage
      selection=$arg ;;
    *) usage ;;
  esac
done
selection=${selection:-all}
services=("$selection")
if [[ $selection == all ]]; then services=(core extensions ui); fi
case "$action" in
  run) [[ $selection != all ]] || usage; run_service "$selection"; exit ;;
  status) for service in "${services[@]}"; do status "$service"; done; exit ;;
  start|stop) ;;
  *) usage ;;
esac

if ! command -v flock >/dev/null; then
  echo "Starting and stopping services requires flock (util-linux) in the Dev Container." >&2
  exit 127
fi

# Signal cancellation before waiting for the launcher lock.
check_stop_request() {
  local target rc
  for target in all "$@"; do
    rc=0
    flock -n -E 42 "$state_dir/stop-$target.lock" true || rc=$?
    if ((rc == 42)); then
      echo "Startup cancelled by stop." >&2
      return 1
    fi
    if ((rc)); then return "$rc"; fi
  done
}
if [[ $action == stop ]]; then
  exec 8>"$state_dir/stop-$selection.lock"
  flock -x 8
fi
# Serialize clicks, including cleanup after a failed launch.
exec 9>"$state_dir/launcher.lock"
flock -x 9
if [[ $action == stop ]]; then
  result=0
  for ((n=${#services[@]}-1; n>=0; n--)); do stop_service "${services[n]}" || result=1; done
  exit "$result"
fi

rebuild=()
if [[ $selection != ui ]]; then
  # The first version in the root POM belongs to its ASF parent.
  project_version=$(sed -n '/<artifactId>streampipes-parent<\/artifactId>/,/<\/version>/s/.*<version>\([^<]*\)<\/version>.*/\1/p' pom.xml)
  if [[ ! $project_version =~ ^[[:alnum:]][[:alnum:]._-]*$ ]]; then
    echo "Cannot read the StreamPipes project version from pom.xml." >&2
    exit 1
  fi
  command -v unzip >/dev/null || { echo "Reading backend JAR manifests requires unzip." >&2; exit 1; }
fi
for service in "${services[@]}"; do
  if managed "$service"; then
    if [[ $service != ui && $build == true ]]; then
      echo "Stop $service before rebuilding it." >&2
      exit 1
    fi
    if [[ $service != ui && $running_version != "$project_version" ]]; then
      echo "$service is running version ${running_version:-unknown}. Stop it before starting $project_version." >&2
      exit 1
    fi
    continue
  fi
  if occupied "$service"; then status "$service" >&2; exit 1; fi
  if [[ $service != ui && ( $build == true || -e $state_dir/$service.needs-build || $(jar_version "$service") != "$project_version" ) ]]; then
    rebuild+=("$service")
  fi
done

if [[ $selection != ui ]]; then
  for url in "http://${SP_COUCHDB_HOST:-couchdb}:${SP_COUCHDB_PORT:-5984}/_up" \
             "http://${SP_NATS_HOST:-nats}:8222/healthz" \
             "http://${SP_INFLUX_HOST:-influxdb}:${SP_INFLUX_PORT:-8086}/health"; do
    echo "Waiting for $url"
    if ! error=$(curl --noproxy '*' -fsS --retry 15 --retry-delay 2 --retry-all-errors --max-time 3 -o /dev/null "$url" 2>&1); then
      echo "Cannot reach $url from the workspace (${error##*$'\n'}). Check the Compose service and its network." >&2
      exit 1
    fi
  done
fi
if [[ $selection == extensions ]] && ! ready core; then
  echo "Start Core before running Extensions on its own." >&2
  exit 1
fi
build_pid=
rollback() {
  local service i
  if [[ -n $build_pid ]]; then
    # Maven and its compiler children have their own process group.
    kill -TERM -- "-$build_pid" 2>/dev/null || kill -TERM "$build_pid" 2>/dev/null || true
    for ((i=0; i<5; i++)); do group_alive "$build_pid" || break; sleep 1; done
    if group_alive "$build_pid"; then kill -KILL -- "-$build_pid" 2>/dev/null || true; fi
    wait "$build_pid" 2>/dev/null || true
  fi
  for service in ui extensions core; do
    if [[ -n ${started[$service]:-} ]]; then stop_service "$service" || true; fi
  done
}
trap rollback EXIT
trap 'exit 130' INT
trap 'exit 143' TERM
check_stop_request "${services[@]}"
if ((${#rebuild[@]})); then
  echo "Building ${rebuild[*]} for StreamPipes $project_version"
  for service in "${rebuild[@]}"; do touch "$state_dir/$service.needs-build"; done
  setsid bash "$script" package-backend "$project_version" "${rebuild[@]}" 9>&- </dev/null &
  build_pid=$!
  while kill -0 "$build_pid" 2>/dev/null; do check_stop_request "${rebuild[@]}"; sleep 1; done
  build_result=0
  wait "$build_pid" || build_result=$?
  build_pid=
  if ((build_result)); then exit "$build_result"; fi
  for service in "${rebuild[@]}"; do
    if [[ $(jar_version "$service") != "$project_version" ]]; then
      echo "Maven did not produce a $project_version Spring Boot JAR at ${jar[$service]}." >&2
      exit 1
    fi
    rm -f -- "$state_dir/$service.needs-build"
  done
  check_stop_request "${services[@]}"
fi

# Registration needs Core. Start both JVMs before probing either HTTP endpoint.
if [[ $selection == all ]] && ! managed core; then
  start_service extensions
  # Core reads this grace period only during the first installation.
  export SP_INITIAL_WAIT_BEFORE_INSTALLATION_MS=${SP_INITIAL_WAIT_BEFORE_INSTALLATION_MS:-30000}
fi
for service in "${services[@]}"; do
  if [[ $service != ui && -z ${started[$service]:-} ]]; then start_service "$service"; fi
done
for service in "${services[@]}"; do
  if [[ $service == ui ]]; then start_service ui; fi
  wait_ready "$service"
  if [[ $service == extensions && ${SP_SETUP_INSTALL_PIPELINE_ELEMENTS:-true} == true ]] && managed core &&
     grep -Fq 'We will perform the initial setup' "$state_dir/core.log"; then
    wait_ready core installation
  fi
done
trap - EXIT
