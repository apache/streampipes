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
  
# End2End tests with cypress
This folder contains a WIP framework for automated E2E tests of StreamPipes.

## How to run the tests?
1. Set up and install a clean StreamPipes instance.
2. Start cypress:
```bash
npm run test-e2e
```

**User**: test@streampipes.apache.org **Password**: test1234

>**Note:** This can be changed in **support/utils/UserUtils.ts**


>**Note:** The base URL can be configured in **cypress.json**

## Design guidlines
* Each test sets up its own test environment (e.g. upload files)
* Once the test is performed all configurations and system changes should be removed (e.g. delete uploaded files)
* There should not be any dependencies between tests

## Directories
* **fixtures**: 
    * Files that are required for tests
* **integrations**: 
    * Contains the actual tests
* **plugins**: 
    * Cypress plugins can be added here
* **support**: 
    * Contains code for the StreamPipes test framework (e.g. model, utils functions, ...)
