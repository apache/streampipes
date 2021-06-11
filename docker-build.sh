#!/bin/sh
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

#!/usr/bin/env bash

repo=apachestreampipes
version=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)

docker_build_amd(){
  echo "Docker build for amd ..."
  docker build --no-cache --pull \
  -t $repo/$1:$version \
  -f $2/Dockerfile $2
}

docker_build_arm(){
  echo "Docker build for arm ..."
  docker buildx build \
  --platform linux/arm/v7 \
  -t $repo/$1:$version-armv7 \
  -f $2/arm.Dockerfile $2 --load
}

docker_build_aarch64(){
  echo "Docker build for aarch64 ..."
  docker buildx build \
  --platform linux/arm64 \
  -t $repo/$1:$version-aarch64 \
  -f $2/aarch64.Dockerfile $2 --load
}

docker_build_multiarch(){
  docker_build_amd $1 $2
  docker_build_arm $1 $2
  docker_build_aarch64 $1 $2
}

echo "Start Docker builds ..."
docker_build_amd backend streampipes-backend
docker_build_amd ui ui
docker_build_multiarch node-controller streampipes-node-controller