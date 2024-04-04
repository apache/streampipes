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
set -e

if [ ! -z "$NGINX_SSL" ] && [ "$NGINX_SSL" = "true" ]; then
    rm /etc/nginx/conf.d/default.conf
    ln -s /app/nginx-confs/ssl.conf /etc/nginx/conf.d/default.conf
elif [ ! -z "$SP_HTTP_SERVER_ADAPTER_ENDPOINT" ]; then

    if [ ! -f /etc/nginx/conf.d/default.conf ]
    then
        DEFAULT_CONF_BACKUP="/etc/nginx/conf.d/default.conf_$(date +%s).bak"
        echo "Create backup of old configuration $DEFAULT_CONF_BACKUP"
        cp /etc/nginx/conf.d/default.conf $DEFAULT_CONF_BACKUP
    fi

    # Required for migration from 0.93.0 to 0.95.0. Could be removed after the release of 0.95.0.
    if [ ! -f /etc/nginx/conf.d/default.conf.template ]
    then
        echo "Migrate to new nginx template"
        cp /etc/opt/nginx/default.conf.template /etc/nginx/conf.d/default.conf.template
    fi

    rm  -f /etc/nginx/conf.d/default.conf
    envsubst '\$SP_HTTP_SERVER_ADAPTER_ENDPOINT' < /etc/nginx/conf.d/default.conf.template > /etc/nginx/conf.d/default.conf
fi

exec "$@"