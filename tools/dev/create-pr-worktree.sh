#!/usr/bin/env bash
#
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
#

set -euo pipefail

usage() {
  cat <<EOF
Create an isolated git worktree for testing a GitHub pull request locally.

Usage:
  tools/dev/create-pr-worktree.sh <pr-number> [worktree-path]

Examples:
  tools/dev/create-pr-worktree.sh 123
  tools/dev/create-pr-worktree.sh 123 ../streampipes-pr-123
EOF
}

if [[ "${1:-}" == "-h" || "${1:-}" == "--help" ]]; then
  usage
  exit 0
fi

if [[ $# -lt 1 || $# -gt 2 ]]; then
  usage
  exit 1
fi

pr_number="$1"

if [[ ! "$pr_number" =~ ^[0-9]+$ ]]; then
  echo "PR number must be numeric: $pr_number" >&2
  exit 1
fi

repo_root="$(git rev-parse --show-toplevel)"
default_worktree_path="$(dirname "$repo_root")/streampipes-pr-$pr_number"
worktree_path="${2:-$default_worktree_path}"
branch_name="pr/$pr_number"
project_name="streampipes-pr-$pr_number"
offset=$((1000 + (pr_number % 100) * 100))

git -C "$repo_root" fetch origin "pull/$pr_number/head:$branch_name"

if [[ -e "$worktree_path" ]]; then
  echo "Worktree path already exists: $worktree_path" >&2
  exit 1
fi

git -C "$repo_root" worktree add "$worktree_path" "$branch_name"

mkdir -p "$worktree_path/.devcontainer"
cat > "$worktree_path/.devcontainer/.env" <<EOF
COMPOSE_PROJECT_NAME=$project_name

SP_CORE_PORT=$((8030 + offset))
SP_EXTENSIONS_IIOT_PORT=$((8090 + offset))
SP_UI_PORT=$((8082 + offset))

SP_COUCHDB_PORT=$((5984 + offset))
SP_TS_STORAGE_PORT=$((8086 + offset))
SP_NATS_PORT=$((4222 + offset))
SP_NATS_MONITORING_PORT=$((8222 + offset))

SP_CORE_DEBUG_PORT=$((5005 + offset))
SP_EXTENSIONS_IIOT_DEBUG_PORT=$((5006 + offset))

SP_INFLUX_INIT_MODE=setup
EOF

cat <<EOF
Created PR worktree:
  $worktree_path

Devcontainer project:
  $project_name

Open the worktree in VS Code and run:
  Dev Containers: Reopen in Container
EOF
