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

NEW_VERSION=$1
# get old version from ./cli/.env
OLD_VERSION=$(grep -oP '^SP_VERSION=\K\S*' ./cli/.env)

FILES=(
  # cli
  "./cli/.env"
  "./cli/README.md"
  # compose
  "./compose/.env"
  "./compose/README.md"
  # k8s
  "./k8s/values.yaml"
  "./k8s/Chart.yaml"
  "./k8s/Chart.yaml"
  "./k8s/README.md"
)

if [ "$#" -gt 1 ]; then
    fatal "Illegal number of arguments"
fi

set_version(){
  options=( "cli" "compose" "k8s" )

  case "$(uname -s)" in
      Darwin)
        # for MacOS
        for file in "${FILES[@]}"; 
        do
          sed -i '' "s/$OLD_VERSION/$NEW_VERSION/g" "$file"
        done
        ;;
      *)
        # for Linux and Windows
        for file in "${FILES[@]}"; 
        do
          sed -i "s/$OLD_VERSION/$NEW_VERSION/g" "$file"
        done
        ;;
  esac

}

set_version

echo "Version updated from $OLD_VERSION to $NEW_VERSION"