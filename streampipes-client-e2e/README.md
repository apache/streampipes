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

# Client E2E

## Environment Setup
Before running, ensure that StreamPipe is operational and the corresponding configuration is input into the `start-streampipes-client-e2e.sh` script.
**Note**: This script must be executed from within the [tool](./tool) directory.
```shell
./start-streampipes-client-e2e.sh -h 127.0.0.1 -p 8030  admin@streampipes.apache.org   -pw admin -t go-client-e2e.sh
```

## Usage Instructions
| Parameter | Default          | Required | Description                                                                                               |
|-----------|-------------------|----------|-----------------------------------------------------------------------------------------------------------|
| `-h`      | `127.0.0.1`       | No       | Host address                                                                                              |
| `-p`      | `8030`            | No       | Backend port                                                                                              |                                                                                                   |
| `-pw`     | `admin`           | No       | Password                                                                                                  |
| `-t`      | `""`              | Yes      | Name of the client's E2E test startup script (the script must be located in the [tool](./tool) directory) |

## How to add E2E in a new language
1. If you need to define an E2E test in a new language, you need to download the element you need to use in `install-element.sh`.
2. To accommodate different languages, you need to write an E2E startup script. You can refer to `go-client-e2e.sh`. The startup script will receive four parameters, so it can easily obtain the information needed to run the Client.

| Parameter | Description |
|-----------|-------------|
| `-h`      | Host        |
| `-p`      | Backend port|
| `-k`      | API Key     |