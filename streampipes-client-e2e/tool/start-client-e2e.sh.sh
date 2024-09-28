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

#!/bin/bash

# Set environment variables
HOST="localhost"
PORT="8030"
LOGIN_URL="/streampipes-backend/api/v2/auth/login"

SP_USERNAME="admin@streampipes.apache.org"
SP_PASSWORD="admin"

API_KEY_URL="/streampipes-backend/api/v2/users/$SP_USERNAME/tokens"
API_KEY_USER_NAME="admin"

loginRequestBody='{
  "username": "'"$SP_USERNAME"'",
  "password": "'"$SP_PASSWORD"'"
}'

# Login and get accessToken
response=$(curl -s -X POST "http://$HOST:$PORT$LOGIN_URL" \
   -H "Content-Type: application/json" \
   -d "$loginRequestBody")
if [ $? -ne 0 ]; then
    echo "Error: Login request failed"
    exit 1
fi

accessToken=$(echo "$response" | sed -n 's/.*"accessToken":"\([^"]*\)".*/\1/p')

if [ -z "$accessToken" ]; then
    echo "Error: Failed to retrieve access token"
    exit 1
fi

apiKeyRequestBody='{
  "tokenName": "'"$API_KEY_USER_NAME"'"
}'

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

cd ../go-client-e2e || exit
go test -v ../go-client-e2e/... -args "$HOST" "$PORT" "$APIKEY" "$API_KEY_USER_NAME"
if [ $? -ne 0 ]; then
    echo "Error: go test failed"
    exit 1
fi

cd ../tool || exit

# add other client test
#...

echo "All tests passed successfully"