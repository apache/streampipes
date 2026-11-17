#
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
#

FROM python:3.12-slim

ENV PYTHONUNBUFFERED=1 PYTHONDONTWRITEBYTECODE=1 PIP_NO_CACHE_DIR=1
WORKDIR /app
# Build from the repository root: docker build -f streampipes-performance-tests/Dockerfile .
COPY streampipes-client-python /src/streampipes-client-python
COPY streampipes-performance-tests/pyproject.toml ./
COPY streampipes-performance-tests/streampipes_performance ./streampipes_performance
COPY streampipes-performance-tests/run_configs ./run_configs
RUN python -m pip install /src/streampipes-client-python . \
    && rm -rf /src \
    && sp-performance validate run_configs/all.json

ENTRYPOINT ["sp-performance"]
