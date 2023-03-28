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
<h3 align="center">Apache StreamPipes for Python</h3>

<p align="center"> Apache StreamPipes meets Python! We are working highly motivated on a Python library to interact with StreamPipes.
In this way, we would like to unite the power of StreamPipes to easily connect to and read different data sources, especially in the IoT domain,
and the amazing universe of data analytics libraries in Python. </p>

---

<br>

**💡The current version of this Python client is still a beta version.**
<br>
**This means that it is still under development, which may result in frequent and extensive API changes, unstable behavior, etc.**
<br>


## 📚 Documentation
Please visit our documentation: https://streampipes.apache.org/docs/docs/python/latest/
There you can find information about how to [get started](https://streampipes.apache.org/docs/docs/python/latest/getting-started/first-steps/),
follow some [tutorials](https://streampipes.apache.org/docs/docs/python/latest/tutorials/1-introduction-to-streampipes-python-client/),
or discover the library via our [references](https://streampipes.apache.org/docs/docs/python/latest/reference/client/client/).
<br>

In case you want to access the documentation of the current development state, you can go here:

👉 [development docs 🤓](https://streampipes.apache.org/docs/docs/python/dev/)

## ⚡️ Quickstart

As a quick example, we demonstrate how to set up and configure a StreamPipes client.

You can simply install the StreamPipes library by running the following command
```bash
pip install streampipes

# if you want to have the current development state you can also execute
pip install git+https://github.com/apache/streampipes.git#subdirectory=streampipes-client-python
```

```python
from streampipes.client import StreamPipesClient
from streampipes.client.config import StreamPipesClientConfig
from streampipes.client.credential_provider import StreamPipesApiKeyCredentials

config = StreamPipesClientConfig(
    credential_provider = StreamPipesApiKeyCredentials(
        username = "test@streampipes.apache.org",
        api_key = "DEMO-KEY",
    ),
    host_address = "localhost",
    https_disabled = True,
    port = 80
)

client = StreamPipesClient(client_config=config)
client.describe()
```

Output:
```
Hi there!
You are connected to a StreamPipes instance running at http://localhost:80.
The following StreamPipes resources are available with this client:
6x DataStreams
1x DataLakeMeasures
```

For more information about how to use the StreamPipes client visit our [introduction tutorial](https://streampipes.apache.org/docs/docs/python/latest/tutorials/1-introduction-to-streampipes-python-client/).
