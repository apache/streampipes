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
# Use an official base image
FROM ubuntu:latest

# Install curl and jq
RUN apt-get update \
    && apt-get install -y curl jq


RUN curl -L -o /usr/local/bin/wait-for-it.sh https://raw.githubusercontent.com/vishnubob/wait-for-it/81b1373f17855a4dc21156cfe1694c31d7d1792e/wait-for-it.sh \
    && chmod +x /usr/local/bin/wait-for-it.sh


COPY upload.sh /usr/local/bin/upload.sh
RUN chmod +x /usr/local/bin/upload.sh


ENV WAITFORIT_TIMEOUT 60

# Run the script
CMD ["/usr/local/bin/wait-for-it.sh", "backend:8030", "--", "/usr/local/bin/upload.sh"]
