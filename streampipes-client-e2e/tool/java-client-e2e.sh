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

E2E_ROOT="$(cd "$(dirname "$0")/.." && pwd)"

HOST=""
PORT=""
APIKEY=""
API_KEY_USER_NAME=""
SCENARIO="${E2E_SCENARIO:-single}"
TEST_CLASS=""

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
        -s)
            SCENARIO="$2"
            shift 2
        ;;
        -c)
            TEST_CLASS="$2"
            shift 2
        ;;
        "")
            break
        ;;
        *)
            shift
        ;;
    esac
done

if [ -z "$TEST_CLASS" ]; then
    case "$SCENARIO" in
        single) TEST_CLASS="JavaClientTest" ;;
        lb) TEST_CLASS="LoadBalanceTest" ;;
        *)
            echo "Error: unknown scenario '$SCENARIO', expected single|lb"
            exit 1
            ;;
    esac
fi

cd "$E2E_ROOT/java-client-e2e" || exit
mvn test -Dtest="$TEST_CLASS" -Dtest.host="$HOST" -Dtest.port="$PORT" -Dtest.apikey="$APIKEY" -Dtest.username="$API_KEY_USER_NAME"
if [ $? -ne 0 ]; then
    echo "Error: java test failed"
    exit 1
fi
echo "All tests passed successfully"
