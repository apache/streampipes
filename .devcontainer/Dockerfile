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

FROM mcr.microsoft.com/devcontainers/base:ubuntu

# Cypress runs its bundled Electron browser even in headless mode. The base
# devcontainer image does not include these desktop runtime libraries, so the
# UI smoke tests fail before Cypress can start. The packages below provide
# audio, GTK/accessibility, NSS, X11, GPU buffer and virtual display support.
RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        libasound2t64 \
        libatk-bridge2.0-0 \
        libgbm1 \
        libglib2.0-0 \
        libgtk-3-0 \
        libnotify4 \
        libnss3 \
        libxss1 \
        libxtst6 \
        xauth \
        xvfb \
    && rm -rf /var/lib/apt/lists/*
