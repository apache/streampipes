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

# run relative to the installer directory, wherever the script is called from
cd "$(dirname "$0")" || exit 1

fatal(){
  echo "Error: $*" >&2
  exit 1
}

if [ "$#" -ne 1 ]; then
  fatal "Usage: $0 <new-version>  (the current version is read from ./cli/.env)"
fi

NEW_VERSION=$1
# get old version from ./cli/.env (portable, no grep -P which BSD grep lacks)
OLD_VERSION=$(sed -n 's/^SP_VERSION=\([^[:space:]]*\).*/\1/p' ./cli/.env)

if [ -z "$OLD_VERSION" ]; then
  fatal "Could not read SP_VERSION from ./cli/.env"
fi

# escape regex metacharacters (e.g. the dots in 0.99.0) so sed matches the version literally
OLD_VERSION_RE=$(printf '%s' "$OLD_VERSION" | sed 's/[][\.*^$/]/\\&/g')

FILES=(
  # cli
  "./cli/.env"
  "./cli/README.md"
  # compose
  "./compose/.env.example"
  "./compose/README.md"
  # k8s
  "./k8s/values.yaml"
  "./k8s/Chart.yaml"
  "./k8s/README.md"
  # all-in-one
  "./all-in-one/Dockerfile"
  "./all-in-one/README.md"
  # root README (try-it-out command)
  "../README.md"
)

set_version(){
  case "$(uname -s)" in
      Darwin)
        # for MacOS
        for file in "${FILES[@]}";
        do
          sed -i '' "s/$OLD_VERSION_RE/$NEW_VERSION/g" "$file"
        done
        ;;
      *)
        # for Linux and Windows
        for file in "${FILES[@]}";
        do
          sed -i "s/$OLD_VERSION_RE/$NEW_VERSION/g" "$file"
        done
        ;;
  esac

}

set_version

echo "Version updated from $OLD_VERSION to $NEW_VERSION"