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

# Builds the all-in-one image from the artifacts of this repository.
#
# Prerequisites (run from the repository root):
#   mvn -pl streampipes-service-core,streampipes-extensions/streampipes-extensions-all-iiot -am -DskipTests package
#   (cd ui && npm ci && npm run build)
#
# Usage: installer/all-in-one/build.sh [image-tag]
# The default tag is apachestreampipes/all-in-one:<project version from the root pom>.

set -euo pipefail

cd "$(dirname "$0")/../.."

CORE_DIR=streampipes-service-core/target
EXTENSIONS_DIR=streampipes-extensions/streampipes-extensions-all-iiot/target
UI_DIR=ui/dist/streampipes/ui/browser

require() {
  if [ ! -e "$1" ]; then
    echo "Missing $1 - $2" >&2
    exit 1
  fi
}

require "$CORE_DIR/streampipes-core.jar" "build it with: mvn -pl streampipes-service-core -am -DskipTests package"
require "$EXTENSIONS_DIR/streampipes-extensions-all-iiot.jar" \
  "build it with: mvn -pl streampipes-extensions/streampipes-extensions-all-iiot -am -DskipTests package"
require "$UI_DIR/index.html" "build it with: cd ui && npm ci && npm run build"

if [ $# -ge 1 ]; then
  TAG="$1"
else
  VERSION=$(sed -n 's/.*<version>\(.*\)<\/version>.*/\1/p' pom.xml | sed -n 2p)
  TAG="apachestreampipes/all-in-one:${VERSION:-latest}"
fi

echo "Building $TAG"
docker build \
  --build-context "core=$CORE_DIR" \
  --build-context "extensions=$EXTENSIONS_DIR" \
  --build-context "ui=$UI_DIR" \
  -t "$TAG" installer/all-in-one
