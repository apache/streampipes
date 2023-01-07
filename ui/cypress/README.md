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
2. Run Cypress (Use one of the three options):

- Open cypress UI:
  ```bash
  npm run test-cypress-open
  ```
- Run smoke tests (tests with suffix .smoke.spec.ts )
  ```bash
  npm run test-cypress-smoke
  ```
- Run whole test suite (tests with suffix .spec.ts )
  ```bash
  npm run test-cypress-all
  ```

**User**: admin@streampipes.apache.org **Password**: admin

> **Note:** To configure the base URL set the environment variable CYPRESS_BASE_URL (e.g. CYPRESS_BASE_URL=http://localhost:8082)

## Design guidlines

- Before each test the whole system is cleaned to have a fresh environment
- Each test sets up its own test environment (e.g. upload files)
- There should not be any dependencies between tests
- Ensure that all services required for the test are running (e.g. external data sources, external databases, ...)

## Automated test runs

- Each night the whole test suite is run within github actions
  - See:
  - To add a test to the test suite add the suffix .spec.ts
- Each PR triggers the smoke tests to detect errors before the branch is merged into the development
  - Add suffix .smoke.spec.ts to add test to the smoke tests
- When no suffix is available the test must be triggered manually

## Directories

- **fixtures**:
  - Files that are required for tests
- **plugins**:
  - Cypress plugins can be added here
- **support**:
  - Contains code for the StreamPipes test framework (e.g. model, utils functions, ...)
- **tests**:
  - Contains the actual test cases grouped by streampipes modules (e.g. connect, pipeline editor, ...)
