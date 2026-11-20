@echo off
rem Licensed to the Apache Software Foundation (ASF) under one or more
rem contributor license agreements.  See the NOTICE file distributed with
rem this work for additional information regarding copyright ownership.
rem The ASF licenses this file to You under the Apache License, Version 2.0
rem (the "License"); you may not use this file except in compliance with
rem the License.  You may obtain a copy of the License at
rem
rem    http://www.apache.org/licenses/LICENSE-2.0
rem
rem Unless required by applicable law or agreed to in writing, software
rem distributed under the License is distributed on an "AS IS" BASIS,
rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
rem See the License for the specific language governing permissions and
rem limitations under the License.

setlocal
if not "%~1"=="" (
  echo Use COMPOSE_FILE or COMPOSE_PROJECT_NAME to select a Compose deployment. 1>&2
  exit /b 1
)
pushd "%~dp0" || exit /b 1
if exist .env goto start
(docker volume ls --format "{{.Name}} {{.Labels}}" && echo inventory-complete) | docker run --rm -i --network none --mount "type=bind,source=%CD%,target=/installer" -w /installer -e COMPOSE_PROJECT_NAME python:3.13-alpine python configure --first-start
if errorlevel 1 goto done
:start
docker compose up -d
:done
set "status=%errorlevel%"
popd
exit /b %status%
