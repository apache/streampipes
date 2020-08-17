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
# StreamPipes Compose - The User's Choice
StreamPipes Compose is a simple collection of user-friendly `docker-compose` files that easily lets gain first-hand experience with Apache StreamPipes.

**Current version:** 0.67.0-SNAPSHOT

> **NOTE**: We recommend StreamPipes Compose to only use for initial try-out and testing. If you are a developer and want to develop new pipeline elements or core feature, use the [StreamPipes CLI](../cli).

#### TL;DR: A one-liner to rule them all :tada: :tada: :tada:

```bash
docker-compose up -d
```
Go to http://localhost to finish the installation in the browser.

## Prerequisite
* Docker >= 17.06.0
* Docker-Compose >= 1.17.0 (Compose file format: 3.4)

Tested on: **macOS, Linux, Windows**

> **NOTE**: On purpose, we disabled all port mappings except of http port **80** to access the StreamPipes UI to provide minimal surface for conflicting ports.

## Usage
We provide two options to get you going:

- **default**: a light-weight option with few pipeline elements, needs less memory
- **full**:  contains more pipeline elements, requires **>16 GB RAM** (recommended)

**Starting** the **default** option is as easy as simply running:
> **NOTE**: Starting might take a while since `docker-compose up` also initially pulls all Docker images from Dockerhub.

```bash
docker-compose up -d
# go to after all services are started http://localhost
```
After all containers are successfully started just got to your browser and visit http://localhost to finish the installation.

**Stopping** the **default** option is similarly easy:
```bash
docker-compose down
# if you want to remove mapped data volumes, run:
# docker-compose down -v
```

Starting the **full** option is almost the same, just specify the `docker-compose.full.yml` file:
```bash
docker-compose -f docker-compose.full.yml up -d
# go to after all services are started http://localhost
```
Stopping the **full** option:
```bash
docker-compose -f docker-compose.full.yml down
#docker-compose -f docker-compose.full.yml down -v
```


## Get help
Since we purely levarage Docker Compose, please see their [documentation](https://docs.docker.com/compose/) in case you want to find out more about their available [commands](https://docs.docker.com/compose/reference/overview/).

If you have any problems during the installation or questions around StreamPipes, you'll get help through one of our community channels:

- [Slack](https://slack.streampipes.org)
- [Mailing Lists](https://streampipes.apache.org/mailinglists.html)

And don't forget to follow us on [Twitter](https://twitter.com/streampipes)!

## License
[Apache License 2.0](../LICENSE)
