<!--
Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License.  You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
-->

# Backend and extensions upgrade smoke test

In GitHub Actions, open **StreamPipes upgrade test**, choose **Run workflow**,
and enter the candidate under **Branch to build and upgrade to** (default
`dev`). The workflow takes the test runner from the workflow ref and builds the
backend and IIoT extensions from the candidate branch.

To run locally from the repository root, build the candidate from the revision you want to release
using JDK 25 and Maven, then run the test with Docker Compose v2, Bash, curl, jq
and OpenSSL available.

```sh
mvn -pl streampipes-service-core,streampipes-extensions/streampipes-extensions-all-iiot -am -DskipTests package
docker build -t streampipes_backend:release-validation streampipes-service-core
docker build -t streampipes_pipeline-elements-all-iiot:release-validation streampipes-extensions/streampipes-extensions-all-iiot
tools/upgrade-test/run.sh
```

The four optional positional arguments select the source backend, candidate backend,
source IIoT extensions, and candidate IIoT extensions, in that order. The defaults
use the published 0.98.0 images and the two local tags built above.

The runner starts a disposable Compose project and uses the source API to create
a dashboard and a pressure simulator with a running data lake persist pipeline.
It captures their IDs and two timestamped pressure values. It then replaces both
application containers while retaining the databases and broker.

After the upgrade and again after a backend restart, the runner checks the original
dashboard, the measurement-to-dataset asset-link migration, and the running IDs
of the original adapter and pipeline. It compares historical pressure samples
through the StreamPipes query API and waits for two distinct samples timestamped
after each startup. Recovery must happen automatically. The runner exits nonzero
on failure and removes the test containers and volumes on exit.
