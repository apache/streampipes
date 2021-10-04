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

dir=docker-images
docker_bundled_edge_tar=bundled-edge-img.tar
docker_bundled_edge_arm_tar=bundled-edge-img-armv7.tar
docker_bundled_edge_aarch64_tar=bundled-edge-img-aarch64.tar
docker_bundled_core_tar=bundled-core-img.tar
docker_bundled_cloud_tar=bundled-cloud-img.tar

docker_img_edge=(
$repo/node-controller:$version \
$repo/extensions-all-jvm:$version \
eclipse-mosquitto:1.6.12 )

docker_img_edge_arm=(
$repo/node-controller:$version-armv7 \
$repo/extensions-all-jvm:$version-armv7 )

docker_img_edge_aarch64=(
$repo/node-controller:$version-aarch64 \
$repo/extensions-all-jvm:$version-aarch64 )

docker_img_core=(
$repo/ui:$version \
$repo/backend:$version \
$repo/pipeline-elements-all-jvm:$version \
fogsyio/activemq:5.15.9 \
fogsyio/consul:1.7.1 \
fogsyio/couchdb:2.3.1 \
fogsyio/kafka:2.2.0 \
fogsyio/zookeeper:3.4.13 \
fogsyio/influxdb:1.7 )

docker_img_cloud=(
$repo/node-controller:$version \
$repo/extensions-all-jvm:$version
)

docker_save_bundle(){
  echo "Start saving Docker images to tar ..."
  create_dir_if_not_exists
  if [ "$1" == "edge" ]; then
      if [ -z "$2" ]; then
        echo "Save edge images (amd) to tar ..."
        docker save ${docker_img_edge[@]} -o $dir/$docker_bundled_edge_tar
      elif [ "$2" == "armv7" ]; then
        echo "Save edge images (armv7) to tar ..." ${docker_img_edge_arm[@]}
        docker save ${docker_img_edge_arm[@]} -o $dir/$docker_bundled_edge_arm_tar
      elif [ "$2" == "aarch64" ]; then
        echo "Save edge images (aarch64) to tar ..." ${docker_img_edge_aarch64[@]}
        docker save ${docker_img_edge_aarch64[@]} -o $dir/$docker_bundled_edge_aarch64_tar
      fi
  elif [ "$1" == "core" ]; then
      echo "Save core images to tar ..."
      docker save ${docker_img_core[@]} -o $dir/$docker_bundled_core_tar
  elif [ "$1" == "cloud" ]; then
      echo "Save cloud images to tar ..."
      docker save ${docker_img_cloud[@]} -o $dir/$docker_bundled_cloud_tar
  else
      echo "Save all images to tar ..."
      docker save ${docker_img_edge[@]} -o $dir/$docker_bundled_edge_tar
      docker save ${docker_img_core[@]} -o $dir/$docker_bundled_core_tar
  fi
}

create_dir_if_not_exists(){
  if [[ ! -e $dir ]]; then
      mkdir $dir
  elif [[ ! -d $dir ]]; then
      echo "$dir already exists but is not a directory" 1>&2
  fi
}

usage() {
  cat <<EOF
Usage: ./docker-save.sh core
       ./docker-save.sh edge
       ./docker-save.sh edge armv7
       ./docker-save.sh edge aarch64
       ./docker-save.sh cloud
EOF
}

if [ -z "$1" ]; then
  usage
else
  docker_save_bundle $1 $2
fi