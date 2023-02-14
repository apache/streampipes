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

# ⚡️ Quickstart

As a quick example, we demonstrate how to set up and configure a StreamPipes client.

```python
>>> from streampipes.client import StreamPipesClient
>>> from streampipes.client.config import StreamPipesClientConfig
>>> from streampipes.client.credential_provider import StreamPipesApiKeyCredentials

>>> config = StreamPipesClientConfig(
...     credential_provider = StreamPipesApiKeyCredentials(
...         username = "test@streampipes.apache.org",
...         api_key = "DEMO-KEY",
...         ),
...     host_address = "localhost",
...     http_disabled = True,
...     port = 80
...)

>>> client = StreamPipesClient(client_config=config)
>>> client.describe()

Hi there!
You are connected to a StreamPipes instance running at http://localhost: 80.
The following StreamPipes resources are available with this client:
6x DataStreams
1x DataLakeMeasures
```
<br>
Alternatively, you can provide your credentials via environment variables.
Simply define your credential provider as follows:

```python
>>> from streampipes.client.credential_provider import StreamPipesApiKeyCredentials

StreamPipesApiKeyCredentials.from_env(username_env="USER", api_key_env="API-KEY")
```
<br>

`username` is always the username that is used to log in into StreamPipes. <br>
The `api_key` can be generated within the UI as demonstrated below:

![Howto API Key](https://raw.githubusercontent.com/apache/streampipes/dev/streampipes-client-python/docs/img/how-to-get-api-key.gif)