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

# Streampipes UI

## Development

Please make sure you have a recent version of [node](https://nodejs.org/en/) and npm (bundled with node) installed.

To build the ui, perform the following steps:

```bash
    npm install
    npm run build
```

To test your local version of the UI execute the following commands:

```bash
    npm install
    npm start
```

(be aware that this requires a StreamPipes instance that runs without the UI, read more [here](https://cwiki.apache.org/confluence/display/STREAMPIPES/UI))

### Formatting and Linting

Running `npm install` will cause a pre-commit hook to be created.
This hook ensures that if you want to commit changes to this repository, these changes are compliant with our [formatting](https://prettier.io/) and [linting](https://eslint.org/) rules.

If you see errors there, you can try to run `npm run format:fix` and `npm run lint:fix` to fix these issues automatically. Otherwise you have to run `npm run format` and `npm run lint` to get additional information about the problems.
