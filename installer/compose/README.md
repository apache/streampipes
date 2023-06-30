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

<!-- BEGIN do not edit: set via ../upgrade_versions.sh -->
**Current version:** 0.93.0-SNAPSHOT
<!-- END do not edit -->

> **NOTE**: We recommend StreamPipes Compose to only use for initial try-out and testing. If you are a developer and want to develop new pipeline elements or core feature, use the [StreamPipes CLI](../cli).

#### TL;DR: A one-liner to rule them all :tada: :tada: :tada:

```bash
docker-compose up -d
```
Go to http://localhost to finish the installation in the browser. Once finished, switch to the pipeline editor and start the interactive tour or check the [online tour](https://streampipes.apache.org/docs/docs/user-guide-tour/) to learn how to create your first pipeline!

## Prerequisite
* Docker >= 17.06.0
* Docker-Compose >= 1.17.0 (Compose file format: 3.4)
* Google Chrome (recommended), Mozilla Firefox, Microsoft Edge

Tested on: **macOS, Linux, Windows 10** (CMD, PowerShell, GitBash)

**macOS** and **Windows 10** (Pro, Enterprise, Education) users can easily get Docker and Docker-Compose on their systems by installing **Docker for Mac/Windows** (recommended).

> **NOTE**: On purpose, we disabled all port mappings except of http port **80** to access the StreamPipes UI to provide minimal surface for conflicting ports.

## Usage
We provide three options to get you going:

- **default**: the standard installation, uses Kafka as internal message broker (recommended)
- **nats**: the standard installation which uses Nats as message broker (recommended for new installations)
- **full**:  contains experimental Flink wrappers

The ``nats`` version will become the default version in a later release. You can already try it for new installations, 
but there's not yet an automatic migration from current Kafka-based installations to Nats.

**Starting** the **default** option is as easy as simply running:
> **NOTE**: Starting might take a while since `docker-compose up` also initially pulls all Docker images from Dockerhub.

```bash
docker-compose up -d
# go to after all services are started http://localhost
```
After all containers are successfully started just got to your browser and visit http://localhost to finish the installation. Once finished, switch to the pipeline editor and start the interactive tour or check the [documentation](https://streampipes.apache.org/docs/docs/user-guide-introduction.html) to learn more about StreamPipes!

**Stopping** the **default** option is similarly easy:
```bash
docker-compose down
# if you want to remove mapped data volumes, run:
# docker-compose down -v
```

Starting the **nats** option works as follows:
```bash
docker-compose -f docker-compose.nats.yml up -d
# go to after all services are started http://localhost
```
Stopping the **nats** option:
```bash
docker-compose -f docker-compose.nats.yml down
#docker-compose -f docker-compose.nats.yml down
```

Starting the **full** option is almost the same, just specify the `docker-compose.full.yml` file:
```bash
docker-compose -f docker-compose.full.yml up -d
# go to after all services are started http://localhost
```
Stopping the **full** option:
```bash
docker-compose -f docker-compose.full.yml down
#docker-compose -f docker-compose.full.yml down
```

## Update services
To actively pull the latest available Docker images use:
```bash
docker-compose pull
# docker-compose -f docker-compose.full.yml pull
```

## Upgrade
To upgrade to another StreamPipes version, simply edit the `SP_VERSION` in the `.env` file.
```
SP_VERSION=<VERSION>
```

## Bugs and Feature Requests

If you've found a bug or have a feature that you'd love to see in StreamPipes, feel free to create an issue i on [GitHub](https://github.com/apache/streampipes/issues).

## Get help
Since we purely levarage Docker Compose, please see their [documentation](https://docs.docker.com/compose/) in case you want to find out more about their available [commands](https://docs.docker.com/compose/reference/overview/).

If you have any problems during the installation or questions around StreamPipes, you'll get help through one of our community channels:

- [Slack](https://slack.streampipes.org)
- [Mailing Lists](https://streampipes.apache.org/mailinglists.html)

And don't forget to follow us on [Twitter](https://twitter.com/streampipes)!

## Contribute
We welcome contributions to StreamPipes. If you are interested in contributing to StreamPipes, let us know! You'll
 get to know an open-minded and motivated team working together to build the next IIoT analytics toolbox.

Here are some first steps in case you want to contribute:
* Subscribe to our dev mailing list [dev-subscribe@streampipes.apache.org](dev-subscribe@streampipes.apache.org)
* Send an email, tell us about your interests and which parts of StreamPipes you'd like to contribute (e.g., core or UI)!
* Ask for a mentor who helps you to understand the code base and guides you through the first setup steps
* Find an issue on [GitHub](https://github.com/apache/streampipes/issues). which is tagged with a _good first issue_ tag
* Have a look at our developer wiki at [https://cwiki.apache.org/confluence/display/STREAMPIPES](https://cwiki.apache.org/confluence/display/STREAMPIPES) to learn more about StreamPipes development.

Have fun!

## Feedback
We'd love to hear your feedback! Subscribe to [users@streampipes.apache.org](mailto:users@streampipes.apache.org)

## License
[Apache License 2.0](../LICENSE)
