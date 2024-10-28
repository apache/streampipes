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


INSTALL_ELEMENT_URL="/streampipes-backend/api/v2/extension-installation"

HOST="127.0.0.1"
PORT="8030"
TOKEN=""


while true; do
    case "$1" in
        -host)
            HOST="$2"
            shift 2
            ;;
        -port)
            PORT="$2"
            shift 2
        ;;
        -token)
            TOKEN="$2"
            shift 2
        ;;
        --help)
            echo "Usage: $0 [-host <ip>] [-port <port>] [-token <username>]"
            exit 0
        ;;
        "")
            #skip directly
            break
        ;;
        *)
          #skip directly
            shift
        ;;
    esac
done


######################Adapters######################

#machine
installRequestBody='{
  "appId":"org.apache.streampipes.connect.iiot.adapters.simulator.machine",
  "publicElement":true,
  "serviceTagPrefix":"ADAPTER"
  }'

response=$(curl -s -X POST "http://$HOST:$PORT$INSTALL_ELEMENT_URL" \
   -H "Content-Type: application/json" \
   -H "authorization: Bearer $TOKEN" \
   -d "$installRequestBody")
if [ $? -ne 0 ]; then
    echo "$response"
    echo "Error install machine adapter"
    exit 1
fi



######################processor######################

#bool_inverter
installRequestBody='{
  "appId":"org.apache.streampipes.processors.transformation.jvm.booloperator.inverter",
  "publicElement":true,
  "serviceTagPrefix":"DATA_PROCESSOR"
  }'

response=$(curl -s -X POST "http://$HOST:$PORT$INSTALL_ELEMENT_URL" \
   -H "Content-Type: application/json" \
   -H "authorization: Bearer $TOKEN" \
   -d "$installRequestBody")
if [ $? -ne 0 ]; then
    echo "$response"
    echo "Error install bool inverter processor"
    exit 1
fi


######################sink######################
#datalake
installRequestBody='{
  "appId":"org.apache.streampipes.sinks.internal.jvm.datalake",
  "publicElement":true,
  "serviceTagPrefix":"DATA_SINK"
  }'

response=$(curl -s -X POST "http://$HOST:$PORT$INSTALL_ELEMENT_URL" \
   -H "Content-Type: application/json" \
   -H "authorization: Bearer $TOKEN" \
   -d "$installRequestBody")
if [ $? -ne 0 ]; then
    echo "$response"
    echo "Error install datalake"
    exit 1
fi