#!/bin/bash

pushd .
cd /etc/apache2

sudo a2enmod proxy_http
sudo a2enmod proxy_wstunnel

popd
