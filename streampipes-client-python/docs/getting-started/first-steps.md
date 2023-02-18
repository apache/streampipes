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

## 📚 Installation

The StreamPipes Python library is meant to work with Python 3.8 and above. Installation can be done via `pip`:
You can install the latest development version from GitHub, as so:

```bash
pip install streampipes
# if you want to have the current development state you can also execute
pip install git+https://github.com/apache/streampipes.git#subdirectory=streampipes-client-python
```

## ⬆️ Setting up StreamPipes
When working with the StreamPipes Python library it is inevitable to have a running StreamPipes instance to connect and interact with.
In case you don't have a running instance at hand, you can easily set up one on your local machine.
Hereby you need to consider that StreamPipes supports different message broker (e.g., Kafka, NATS).
Although the Python library aims to support all of them equally, we encourage you to run StreamPipes with the NATS protocol as the messaging layer.
If you are using a different messaging broker and experience a problem, please do not hesitate to contact us.
In case you are unsure if it is indeed a bug, please feel free to start a [discussion](https://github.com/apache/streampipes/discussions) on GitHub.
Alternatively, file us a bug in form of a GitHub [issue](https://github.com/apache/streampipes/issues/new/choose).
<br>
The following shows how you can set up a StreamPipes instance that uses [NATS](https://docs.nats.io/) as messaging layer.

### 🐳 Start StreamPipes via Docker Compose
The easiest and therefore recommend way to get StreamPipes started is by using [docker compose](https://docs.docker.com/compose/).
Therefore, you need Docker running. You can check if Docker is ready on your machine by executing.
````bash
docker ps
````
If this results in an output similar to the following, Docker is ready to continue.
```
CONTAINER ID   IMAGE     COMMAND   CREATED   STATUS    PORTS     NAMES
...            ...       ...       ...       ...       ...       ...
```
Otherwise, you need to start docker first.

Please read the full guide on how to start StreamPipes with `docker compose` [here](https://github.com/apache/streampipes/blob/dev/installer/compose/README.md).
So in our scenario, we will go with `docker-compose.nats.yml` to use NATS as messaging backend. 
Thereby, when running locally, we need to add the following port mapping entry to `nats.ports`:
```yaml
- 4222:4222
```

After this modification is applied, StreamPipes can simply be started with this command:
```bash
docker-compose -f docker-compose.nats.yml up -d
```

Once all services are started, you can access StreamPipes via  `http://localhost`.

Have fun discovering StreamPipes and our Python library 🚀
