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
   <img src="https://raw.githubusercontent.com/apache/incubator-streampipes/dev/streampipes-client-python/docs/img/streampipes-python.png"
   alt="StreamPipes Logo with Python" title="Apache StreamPipes Logo with Python" width="75%"/>
  <br>
</h1>
<h4 align="center"><a href="[StreamPipes](https://github.com/apache/incubator-streampipes)">StreamPipes</a> is a self-service (Industrial) IoT toolbox to enable non-technical users to connect , analyze and explore IoT data streams.</h4>

<br>
<h3 align="center">Apache StreamPipes client for Python</h3>

<p align="center"> Apache StreamPipes meets Python! We are working highly motivated on a Python-based client to interact with StreamPipes.
In this way, we would like to unite the power of StreamPipes to easily connect to and read different data sources, especially in the IoT domain,
and the amazing universe of data analytics libraries in Python. </p>

---

<br>

** ❗❗❗IMPORTANT ❗❗❗**
<br>
<br>
**The current version of this Python client is still in alpha phase at best.**
<br>
**This means that it is still heavily under development, which may result in frequent and extensive API changes, unstable behavior, etc.**
<br>
**Please consider it only as a sneak preview.**
<br>
<br>
**❗❗❗ IMPORTANT ❗❗❗**

<br>

## ⚡️ Quickstart

As a quick example, we demonstrate how to set up and configure a StreamPipes client.
In addition, we will get the available data lake measures out of StreamPipes.

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

# get all available datat lake measures
>>> measures = client.dataLakeMeasureApi.all()

# get amount of retrieved measures
>>> len(measures)
1

# inspect the data lake measures as pandas dataframe
>>> measures.to_pandas()
    measure_name timestamp_field  ... pipeline_is_running num_event_properties
0           test   s0::timestamp  ...               False                    2
[1 rows x 6 columns]
```
<br>
Alternatively, you can provide your credentials via environment variables.
Simply define your credential provider as follows:

```python
>>> from streampipes_client.client.credential_provider import StreamPipesApiKeyCredentials

StreamPipesApiKeyCredentials.from_env(username_env="USER", api_key_env="API-KEY")
```
<br>

`username` is always the username that is used to log in into StreamPipes. <br>
The `api_key` can be generated within the UI as demonstrated below:

![Howto API Key](https://raw.githubusercontent.com/apache/incubator-streampipes/dev/streampipes-client-python/docs/img/how-to-get-api-key.gif)