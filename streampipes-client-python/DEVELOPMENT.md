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

# Development Guide
This document describes how to easily set up your local dev environment to work on the
StreamPipes Python client :snake:.

### First Steps :rocket:
<br>

1) **Set up your Python environment**

Create a virtual Python environment with a tool of your choice.
As a next step, install all required dependencies for the development, e.g., with `pip`:

```
pip install .[dev]  # or alternatively: pip install .[all] to include dependencies for building the docs as well
```

In case you are on macOS and using `zsh` the following should work for you:
```
pip install ."[dev]"
```
<br>

2) **Install pre-commit hook**

The pre-commit hook is run before every commit and takes care about code style,
linting, type hints, import sorting, etc. It will stop your commit in case the changes do not apply the expected format.
Always check to have the recent version of the pre-commit installed otherwise the CI build might fail:

```
pre-commit install
```
The definition of the pre-commit hook can be found in [.pre-commit-config.yaml](.pre-commit-config.yaml).

<br>

### Conventions :clap:
Below we list some conventions that we have agreed on for creating the StreamPipes Python client.
Please comply to them when you plan to contribute to this project.
If you have any other suggestions or would like to discuss them, we would be happy to hear from you on our mailing list [dev@streampipes.apache.org](mailto:dev@streampipes.apache.org).

1) **Use `numpy` style for Python docstrings** :page_facing_up: <br>
Please stick to the `numpy` style when writing docstrings, as we require this for generating our documentation.


2) **Provide tests** :white_check_mark: <br>
We are aiming for broad test coverage for the Python client and
have therefore set a requirement of at least 90% unit test coverage.
Therefore, please remember to write (unit) tests already during development. 
If you have problems with writing tests, don't hesitate to ask us for help directly in the PR or
even before that via our mailing list (see above).


<!---
TODO: replace link to java file by link to documentation
--->
3) **Build a similar API as the Java client provides** :arrows_clockwise: <br>
Whenever possible, please try to develop the API of the Python client the same as the [Java client](../streampipes-client/src/main/java/org/apache/streampipes/client/StreamPipesClient.java).
By doing so, we would like to provide a consistent developer experience and the basis for automated testing in the future.
