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

#!/bin/bash
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

while true; do
  if curl -s "http://backend:8030/streampipes-backend/api/v2/auth/login" --max-time 10 &> /dev/null; then
    echo "StreamPipes backend is now ready!"
    break
  else
    echo "StreamPipes backend not ready, waiting for 3 seconds..."
    sleep 3
  fi
done

JSON_TOKEN_RESPONSE=$(curl -s -X POST "http://backend:8030/streampipes-backend/api/v2/auth/login" \
    -H "Content-Type: application/json" \
    -d "{\"username\":\"$USERNAME\",\"password\":\"$PASSWORD\"}")

TOKEN=$(echo "$JSON_TOKEN_RESPONSE" | jq -r '.accessToken')
RESPONSE_TOKEN="Bearer $TOKEN"

JSON_RESOURCE_ITEMS=$(curl -s -X GET "http://backend:8030/streampipes-backend/api/v2/rdfendpoints/items" \
    -H "Content-Type: application/json" \
    -H "Authorization: $RESPONSE_TOKEN")

ARRAY_LENGTH=$(echo $JSON_RESOURCE_ITEMS | jq '. | length')

while [ $ARRAY_LENGTH -le 100 ]; do
    echo "StreamPipes Extensions Service not ready, waiting for 3 seconds..."
    sleep 3
    JSON_RESOURCE_ITEMS=$(curl -s -X GET "http://backend:8030/streampipes-backend/api/v2/rdfendpoints/items" \
        -H "Content-Type: application/json" \
        -H "Authorization: $RESPONSE_TOKEN")
    ARRAY_LENGTH=$(echo $JSON_RESOURCE_ITEMS | jq '. | length')
done

ITEM_MAP_NAME_URI=$(echo $JSON_RESOURCE_ITEMS | jq -r 'map({(.name): .uri}) | add')

ITEM_MAP_NAME_URI=$(echo $JSON_RESOURCE_ITEMS | jq -r 'map({(.name): .uri}) | add')

for ZIP_FILE in /zip_folder/*.zip; do
  JSON_RESPONSE=$(curl --compressed -X POST "http://backend:8030/streampipes-backend/api/v2/import/preview" \
      -H "Authorization: $RESPONSE_TOKEN" \
      -F "file_upload=@$ZIP_FILE")

  PIPELINE_IDS=($(echo "$JSON_RESPONSE" | jq -r '.pipelines[].resourceId'))

  for id in "${PIPELINE_IDS[@]}"; do

    ITEMS_PIPELINES=$(curl -s -X GET "http://backend:8030/streampipes-backend/api/v2/pipelines/$id" \
        -H "Content-Type: application/json" \
        -H "Authorization: $RESPONSE_TOKEN")

    KEYS=$(echo $ITEMS_PIPELINES | jq -r 'keys[]')

    for key in $KEYS; do
      if [ "$(echo $ITEMS_PIPELINES | jq ".${key} | type")" == '"array"' ]; then
        key=$(echo $ITEMS_PIPELINES | jq -r ".${key}[]? | .name")
        value=$(echo $ITEM_MAP_NAME_URI | jq -r ".\"$key\"")

        curl -s -X POST "http://backend:8030/streampipes-backend/api/v2/element" \
            -H "Content-Type: application/x-www-form-urlencoded;charset=UTF-8" \
            -H "Authorization: $RESPONSE_TOKEN" \
            --data-urlencode "uri=$value" \
            --data-urlencode "publicElement=true"
      fi
    done
  done

  JSON_PAYLOAD="$JSON_RESPONSE"

  curl -i -X POST "http://backend:8030/streampipes-backend/api/v2/import" \
      -H "Authorization: $RESPONSE_TOKEN" \
      -F "file_upload=@$ZIP_FILE" \
      -F "configuration=@-;type=application/json" <<< "$JSON_PAYLOAD"
done
