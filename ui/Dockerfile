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
ARG BUILDER_IMAGE=node:13.8.0-alpine3.11
ARG BASE_IMAGE=nginx

FROM $BUILDER_IMAGE AS builder

ENV NODE_PATH=/cache/node_modules \
    NODE_OPTIONS="--max_old_space_size=6096"

WORKDIR /builder/app

COPY . ./
RUN npm install --unsafe-perm; \
    npm run build

FROM $BASE_IMAGE

COPY --from=builder /builder/app/dist/ /usr/share/nginx/html/

COPY nginx_config/nginx.conf /etc/nginx/nginx.conf
COPY nginx_config/default.conf /etc/nginx/conf.d/default.conf
COPY nginx_config/ssl.conf /app/nginx-confs/ssl.conf

COPY docker-entrypoint.sh /

RUN chown -R nginx:nginx /usr/share/nginx/html/

ENTRYPOINT ["/docker-entrypoint.sh"]
CMD ["nginx", "-g", "daemon off;"]