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

mvn archetype:generate -DarchetypeGroupId=org.apache.streampipes \
	-DarchetypeArtifactId=streampipes-archetype-extensions-jvm -DarchetypeVersion=0.70.0 \
	-DgroupId=org.streampipes.test -DartifactId=automated-test -DclassNamePrefix=AutomatedTest -DpackageName=test -DinteractiveMode=false

cd automated-test
mvn clean package

cd ..
docker-compose build
docker-compose up -d
sleep 30

cd backend
docker-compose up -d
sleep 30

cd ../../../../../
ls
npx cypress run --spec 'cypress/tests/experimental/testJvmArchetype/testJvmArchetype.ts'
ls
cd cypress/tests/experimental/testJvmArchetype/backend/
docker-compose down -v

cd ..
docker-compose down -v

rm -r automated-test
