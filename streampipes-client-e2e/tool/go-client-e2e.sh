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

# Set environment variables
HOST=""
PORT=""
APIKEY=""
API_KEY_USER_NAME=""

while true; do
    case "$1" in
        -h)
            HOST="$2"
            shift 2
        ;;
        -p)
            PORT="$2"
            shift 2
        ;;
        -u)
            API_KEY_USER_NAME="$2"
            shift 2
        ;;
        -k)
            APIKEY="$2"
            shift 2
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

cd ../go-client-e2e || exit
go test -v ../go-client-e2e/... -args "$HOST" "$PORT" "$APIKEY" "$API_KEY_USER_NAME"
if [ $? -ne 0 ]; then
    cd ../tool
    echo "Error: go test failed"
    exit 1
fi
cd ../tool || exit
echo "All tests passed successfully"