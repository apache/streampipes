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

FROM nginx:mainline-alpine3.18

COPY dist/streampipes/ui/browser/ /usr/share/nginx/html/

#Store a copy to have a version if the other is hidden in the volume. Required for migration from 0.93.0 to 0.95.0. Could be removed after the release of 0.95.0.
RUN mkdir -p /etc/opt/nginx/
RUN chown -R nginx:nginx /etc/opt/nginx/
COPY nginx_config/default.conf.template /etc/opt/nginx/default.conf.template
RUN chown -R nginx:nginx  /etc/opt/nginx/default.conf.template



RUN chown -R nginx:nginx /usr/share/nginx/html && chmod -R 755 /usr/share/nginx/html && \
        chown -R nginx:nginx /var/cache/nginx && \
        chown -R nginx:nginx /var/log/nginx && \
        chown -R nginx:nginx /etc/nginx/conf.d
RUN touch /var/run/nginx.pid && \
        chown -R nginx:nginx /var/run/nginx.pid

RUN chown -R nginx:nginx /etc/nginx/
RUN chown -R nginx:nginx /usr/share/nginx/html/
RUN chown -R nginx:nginx /var/cache/nginx/
RUN chown -R nginx:nginx /var/run/

USER nginx

COPY nginx_config/nginx.conf /etc/nginx/nginx.conf
COPY nginx_config/default.conf /etc/nginx/conf.d/default.conf
COPY nginx_config/default.conf.template /etc/nginx/conf.d/default.conf.template
COPY nginx_config/ssl.conf /app/nginx-confs/ssl.conf

COPY docker-entrypoint.sh /

RUN mkdir -p /var/cache/nginx/client_temp
RUN mkdir -p /var/cache/nginx/proxy_temp
RUN mkdir -p /var/cache/nginx/fastcgi_temp
RUN mkdir -p /var/cache/nginx/scgi_temp
RUN mkdir -p /var/cache/nginx/uwsgi_temp

USER 101:101

ENTRYPOINT ["sh", "/docker-entrypoint.sh"]
CMD ["nginx", "-g", "daemon off;"]
