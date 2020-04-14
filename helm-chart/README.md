<!--
  ~ Licensed to the Apache Software Foundation (ASF) under one or more
  ~ contributor license agreements.  See the NOTICE file distributed with
  ~ this work for additional information regarding copyright ownership.
  ~ The ASF licenses this file to You under the Apache License, Version 2.0
  ~ (the "License"); you may not use this file except in compliance with
  ~ the License.  You may obtain a copy of the License at
  ~
  ~    http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  ~
  -->

# StreamPipes Helm Chart
StreamPipes helm chart to deploy StreamPipes on Kubernetes.

Currently: **StreamPipes v0.65.1-SNAPSHOT**

## Prerequisite

Requires Helm (https://helm.sh/) and an active connection to a kubernetes cluster with a running tiller server.

Tested with:
* Kubernetes v1.17.4
* Helm v3.1.2

## Start StreamPipes

Run command:

```sh
helm install streampipes ./ --set deployment=lite
```

```sh
helm install streampipes ./ --set deployment=full
```

## Delete StreamPipes

```sh
helm del streampipes
```
