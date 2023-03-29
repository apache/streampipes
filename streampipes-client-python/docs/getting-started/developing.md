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

## 📖 Development Guide
This document describes how to easily set up your local dev environment to work on StreamPipes Python 🐍.
<br>

### 🚀 First Steps

1) **Set up your Python environment**

Create a virtual Python environment with a tool of your choice.
As a next step, install all required dependencies for the development, e.g., with `pip`:

```bash
pip install .[dev]  # or alternatively: pip install .[all] to include dependencies for building the docs as well
```

In case you are on macOS and using `zsh` the following should work for you:
```bash
pip install ."[dev]"
```
<br>

2) **Install pre-commit hook**

The pre-commit hook is run before every commit and takes care about code style,
linting, type hints, import sorting, etc. It will stop your commit in case the changes do not apply the expected format.
Always check to have the recent version of the pre-commit hook installed otherwise the CI build might fail. 
If you are interested, you can have a deeper look on the underlying library: [pre-commit](https://pre-commit.com/).

```bash
pre-commit install
```
The definition of the pre-commit hook can be found in [.pre-commit-config.yaml](https://github.com/apache/streampipes/blob/dev/streampipes-client-python/.pre-commit-config.yaml).

<br>

### 👏 Conventions
Below we list some conventions that we have agreed on for creating StreamPipes Python.
Please comply to them when you plan to contribute to this project.
If you have any other suggestions or would like to discuss them, we would be happy to hear from you on our mailing list [dev@streampipes.apache.org](mailto:dev@streampipes.apache.org)
or in our [discussions](https://github.com/apache/streampipes/discussions) on GitHub.

1) **Use `numpy` style for Python docstrings** 📄 <br>
Please stick to the `numpy` [style](https://numpydoc.readthedocs.io/en/latest/format.html) when writing docstrings, as we require this for generating our documentation.


2) **Provide tests** ✅ <br>
We are aiming for broad test coverage for the Python package and
have therefore set a requirement of at least 90% unit test coverage.
Therefore, please remember to write (unit) tests already during development. 
If you have problems with writing tests, don't hesitate to ask us for help directly in the PR or
even before that via our mailing list (see above).


<!---
TODO: replace link to java file by link to documentation
--->
3) **Build a similar API as the Java client provides** 🔄 <br>
Whenever possible, please try to develop the API of the Python library the same as the [Java client](https://github.com/apache/streampipes/blob/dev/streampipes-client/src/main/java/org/apache/streampipes/client/StreamPipesClient.java) or Java SDK.
By doing so, we would like to provide a consistent developer experience and the basis for automated testing in the future.

---
## 🚀 Roadmap

Broadly speaking, we plan to expand or add new aspects/functionality to the library where we are focusing on the following:

- increase coverage of StreamPipes API 🔗
- build a comprehensive function zoo 🐘
- support more messaging broker 📬
- possibility to build pipeline elements 🔧

In case you want to have a more detailed look on what we are currently planning, have a look at our [open issues](https://github.com/apache/streampipes/labels/python)(more short-term driven).

Of course, contributions are always highly appreciated 🔮

Stay tuned!

---
## 👨‍💻 Contributing
*Before opening a pull request*, review the [Get Involved](https://streampipes.apache.org/getinvolved.html) page.
It lists information that is required for contributing to StreamPipes.

When you contribute code, you affirm that the contribution is your original work and that you
license the work to the project under the project's open source license. Whether or not you
state this explicitly, by submitting any copyrighted material via pull request, email, or
other means you agree to license the material under the project's open source license and
warrant that you have the legal authority to do so.

---