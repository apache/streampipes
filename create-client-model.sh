#!/bin/sh
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

# generate model
mvn typescript-generator:generate -f streampipes-model/pom.xml

# generate model-client
mvn typescript-generator:generate -f streampipes-model-client/pom.xml

# copy files
mv streampipes-model/target/typescript-generator/streampipes-model.ts ui/projects/streampipes/platform-services/src/lib/model/gen/
mv streampipes-model-client/target/typescript-generator/streampipes-model-client.ts ui/projects/streampipes/platform-services/src/lib/model/gen/