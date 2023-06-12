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
# the corresponding documentation can be found here: https://streampipes.apache.org/docs/docs/python/dev/
```

## ⬆️ Setting up StreamPipes
When working with the StreamPipes Python library it is inevitable to have a running StreamPipes instance to connect and interact with.
In case you don't have a running instance at hand, you can easily set up one on your local machine.
Hereby you need to consider that StreamPipes supports different message broker (e.g., Kafka, NATS).
We will demonstrate below how you can easily set up StreamPipes for both supported message brokers.
<br>

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
Please read the full guide on how to start StreamPipes with `docker compose` [here](https://streampipes.apache.org/docs/docs/deploy-docker.html).

#### Setup StreamPipes with NATS as message broker
The following shows how you can set up a StreamPipes instance that uses [NATS](https://docs.nats.io/) as messaging layer.
So in this scenario, we will go with `docker-compose.nats.yml`. 
Thereby, when running locally, we need to add the following port mapping entry to `services.nats.ports`:
```yaml
- 4222:4222
```

After this modification is applied, StreamPipes can simply be started with this command:
```bash
docker-compose -f docker-compose.nats.yml up -d
```

Once all services are started, you can access StreamPipes via `http://localhost`.

#### Setup StreamPipes with Kafka as message broker
Alternatively, you can use `docker-compose.yml` to start StreamPipes with Kafka as messaging layer.
When running locally we have to modify `services.kafka.environment` and add the ports to `services.kafka.ports`:
```yaml
environment:
  KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,OUTSIDE:PLAINTEXT
  KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://:9092,OUTSIDE://localhost:9094
  KAFKA_LISTENERS: PLAINTEXT://:9092,OUTSIDE://:9094
...
ports:
  - 9094:9094
```
Then, you need to execute the following command:
```bash
docker-compose -f docker-compose.yml up -d
```

Once all services are started, you can access StreamPipes via `http://localhost`.

In case you want to have more control over your StreamPipes setup,
you might take a look at our [deployment CLI](https://streampipes.apache.org/docs/docs/extend-cli.html).

Have fun discovering StreamPipes and our Python library 🚀
