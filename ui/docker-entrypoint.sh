#!/bin/bash
set -e

if [ ! -z "$NGINX_SSL" ] && [ "$NGINX_SSL" = "true" ]; then

    rm /etc/nginx/conf.d/default.conf
    ln -s /app/nginx-confs/ssl.conf /etc/nginx/conf.d/default.conf

fi

exec "$@"