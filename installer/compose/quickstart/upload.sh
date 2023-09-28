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


# Send a POST request and save the response as JSON

if [ -z "$SP_INITIAL_ADMIN_EMAIL" ]; then
  USERNAME="admin@streampipes.apache.org"
else
  USERNAME="$SP_INITIAL_ADMIN_EMAIL"
fi

if [ -z "$SP_INITIAL_ADMIN_PASSWORD" ]; then
  PASSWORD="admin"
else
  PASSWORD="$SP_INITIAL_ADMIN_PASSWORD"
fi


# Login and obtain token
JSON_TOKEN_RESPONSE=$(curl -s -X POST "http://backend:8030/streampipes-backend/api/v2/auth/login" \
    -H "Content-Type: application/json" \
    -d "{\"username\":\"$USERNAME\",\"password\":\"$PASSWORD\"}")

TOKEN=$(echo "$JSON_TOKEN_RESPONSE" | jq -r '.accessToken')
RESPONSE_TOKEN="Bearer $TOKEN"



for ZIP_FILE in /zip_folder/*.zip; do
  echo "Processing $ZIP_FILE..."
  # POST preview request with curl
  JSON_RESPONSE=$(curl --compressed -X POST "http://backend:8030/streampipes-backend/api/v2/import/preview" \
      -H "Authorization: $RESPONSE_TOKEN" \
      -F "file_upload=@$ZIP_FILE")


  JSON_PAYLOAD="$JSON_RESPONSE"

  # POST upload request using curl
  curl -i -X POST "http://backend:8030/streampipes-backend/api/v2/import" \
      -H "Authorization: $RESPONSE_TOKEN" \
      -F "file_upload=@$ZIP_FILE" \
      -F "configuration=@-;type=application/json" <<< "$JSON_PAYLOAD"
done

