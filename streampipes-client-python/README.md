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


<h1 align="center">
  <br>
   <img src="https://raw.githubusercontent.com/apache/streampipes/dev/streampipes-client-python/docs/img/streampipes-python.png"
   alt="StreamPipes Logo with Python" title="Apache StreamPipes Logo with Python" width="75%"/>
  <br>
</h1>
<h4 align="center"><a href="[StreamPipes](https://github.com/apache/streampipes)">StreamPipes</a> is a self-service (Industrial) IoT toolbox to enable non-technical users to connect , analyze and explore IoT data streams.</h4>

<br>
<h3 align="center">Apache StreamPipes client for Python</h3>

<p align="center"> Apache StreamPipes meets Python! We are working highly motivated on a Python-based client to interact with StreamPipes.
In this way, we would like to unite the power of StreamPipes to easily connect to and read different data sources, especially in the IoT domain,
and the amazing universe of data analytics libraries in Python. </p>

---

<br>

**:exclamation::exclamation::exclamation:IMPORTANT:exclamation::exclamation::exclamation:**
<br>
<br>
**The current version of this Python client is still in alpha phase at best.**
<br>
**This means that it is still heavily under development, which may result in frequent and extensive API changes, unstable behavior, etc.**
<br>
**Please consider it only as a sneak preview.**
<br>
<br>
**:exclamation::exclamation::exclamation:IMPORTANT:exclamation::exclamation::exclamation:**

<br>

## ⚡️ Quickstart

As a quick example, we demonstrate how to set up and configure a StreamPipes client.

```python
>>> from streampipes_client.client import StreamPipesClient
>>> from streampipes_client.client.client_config import StreamPipesClientConfig
>>> from streampipes_client.client.credential_provider import StreamPipesApiKeyCredentials

>>> config = StreamPipesClientConfig(
...    credential_provider=StreamPipesApiKeyCredentials(
...         username="test@streampipes.apache.org",
...         api_key="DEMO-KEY",
...         ),
...     host_address="localhost",
...     http_disabled=True,
...     port=80                  
...)

>>> client = StreamPipesClient(client_config=config)
>>> client.describe()

Hi there!
You are connected to a StreamPipes instance running at http://localhost:80.
The following StreamPipes resources are available with this client:
6x DataStreams
1x DataLakeMeasures
```

For more information about how to use the StreamPipes client visit our [introduction example]().