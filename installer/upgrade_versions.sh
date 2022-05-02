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

if [ "$#" -gt 1 ]; then
    fatal "Illegal number of arguments"
fi

set_version(){
  options=( "cli" "compose" "k8s" )

  case "$(uname -s)" in
      Darwin)
        # for MacOS
        sed -i '' 's/SP_VERSION=.*/SP_VERSION='$NEW_VERSION'/g' ./cli/.env
        sed -i '' 's/SP_VERSION=.*/SP_VERSION='$NEW_VERSION'/g' ./compose/.env
        sed -i '' 's/  version: .*/  version: "'$NEW_VERSION'"/g' ./k8s/values.yaml
        sed -i '' 's/appVersion: .*/appVersion: "'$NEW_VERSION'"/g' ./k8s/Chart.yaml
        sed -i '' 's/version: .*/version: '$NEW_VERSION'/g' ./k8s/Chart.yaml

        for opt in "${options[@]}"
        do
          sed -i '' 's/\*\*Current version:\*\* .*/\*\*Current version:\*\* '$NEW_VERSION'/g' ./$opt/README.md
        done
        ;;
      *)
        # for Linux and Windows
        sed -i 's/SP_VERSION=.*/SP_VERSION='$NEW_VERSION'/g' ./cli/.env
        sed -i 's/SP_VERSION=.*/SP_VERSION='$NEW_VERSION'/g' ./compose/.env
        sed -i 's/  version: .*/  version: "'$NEW_VERSION'"/g' ./k8s/values.yaml
        sed -i 's/appVersion: .*/appVersion: "'$NEW_VERSION'"/g' ./k8s/Chart.yaml
        sed -i 's/version: .*/version: '$NEW_VERSION'/g' ./k8s/Chart.yaml

        for opt in "${options[@]}"
        do
          sed -i 's/\*\*Current version:\*\* .*/\*\*Current version:\*\* '$NEW_VERSION'/g' ./$opt/README.md
        done
        ;;
  esac
}

set_version
