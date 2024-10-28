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
E2E_TEST=""
HOST="127.0.0.1"
PORT="8030"
LOGIN_URL="/streampipes-backend/api/v2/auth/login"

SP_USERNAME="admin@streampipes.apache.org"
SP_PASSWORD="admin"

while true; do
    case "$1" in
        -u)
            SP_USERNAME="$2"
            shift 2
            ;;
        -pw)
            SP_PASSWORD="$2"
            shift 2
        ;;
        -h)
            HOST="$2"
            shift 2
        ;;
        -p)
            PORT="$2"
            shift 2
        ;;
        -t)
            E2E_TEST="$2"
            shift 2
        ;;
        --help)
            echo "Usage: $0 [-h <ip>] [-p <port>] [-u <username>] [-pw <password>] [-t <E2E_TEST>]"
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

if [ E2E_TEST == "" ]; then
    echo "-t is empty"
    exit 1
fi

API_KEY_URL="/streampipes-backend/api/v2/users/$SP_USERNAME/tokens"
API_KEY_USER_NAME="$SP_USERNAME"

loginRequestBody='{
  "username": "'"$SP_USERNAME"'",
  "password": "'"$SP_PASSWORD"'"
}'

if nc -zv localhost 8030; then
    echo "Port 8030 is open and listening."
else
    echo "Port 8030 is not open or not listening."
    exit 1
fi

echo "start login"
max_attempts=60
attempt=1
while [ $attempt -le $max_attempts ]
do
    response=$(curl -s -X POST "http://$HOST:$PORT$LOGIN_URL" \
        -H "Content-Type: application/json" \
        -d "$loginRequestBody")
    if [ $? -eq 0 ]; then
        echo "Login successful"
        break
    else
        echo "$response"
        echo "Error: Login request failed on attempt $attempt"
        if [ $attempt -eq $max_attempts ]; then
            echo "Max attempts reached. Exiting."
            exit 0
        else
            echo "Retrying in 1 second..."
            sleep 1
        fi
    fi
    attempt=$((attempt+1))
done

echo "get token"
accessToken=$(echo "$response" | sed -n 's/.*"accessToken":"\([^"]*\)".*/\1/p')
if [ -z "$accessToken" ]; then
    echo "Error: Failed to retrieve access token"
    exit 1
fi

apiKeyRequestBody='{
  "tokenName": "'"$API_KEY_USER_NAME"'"
}'

echo "install element"
chmod +x ./install-element.sh
./install-element.sh -host "$HOST" -port "$PORT" -token "$accessToken"

echo "get apikey"
# Get APIKEY
APIKEYRESP=$(curl -s -X POST "http://$HOST:$PORT$API_KEY_URL" \
   -H "Content-Type: application/json" \
   -H "authorization: Bearer $accessToken" \
   -d "$apiKeyRequestBody")
if [ $? -ne 0 ]; then
    echo "Error: API Key request failed"
    exit 1
fi
APIKEY=$(echo "$APIKEYRESP" | sed 's/.*"rawToken":"\([^"]*\)".*/\1/')
if [ -z "$APIKEY" ]; then
    echo "Error: Failed to retrieve API key"
    exit 1
fi

echo "start e2e test"
chmod +x ./"$E2E_TEST"
./"$E2E_TEST" -h "$HOST" -p "$PORT" -u "$API_KEY_USER_NAME" -k "$APIKEY"
if [ $? -ne 0 ]; then
    echo "start $E2E_TEST failed"
    exit 1
fi

echo "All tests passed successfully"