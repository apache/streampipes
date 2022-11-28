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

[![Github Actions](https://img.shields.io/github/workflow/status/apache/streampipes/build-and-deploy-docker-dev)](https://github.com/apache/streampipes/actions/)
[![Docker pulls](https://img.shields.io/docker/pulls/apachestreampipes/backend.svg)](https://hub.docker.com/r/apachestreampipes/backend/)
[![Maven central](https://img.shields.io/maven-central/v/org.apache.streampipes/streampipes-backend.svg)](https://img.shields.io/maven-central/v/org.apache.streampipes/streampipes-backend.svg)
[![License](https://img.shields.io/github/license/apache/streampipes.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![Last commit](https://img.shields.io/github/last-commit/apache/streampipes.svg)]()
[![Twitter](https://img.shields.io/twitter/follow/StreamPipes.svg?label=Follow&style=social)](https://twitter.com/StreamPipes)
[![Apache StreamPipes](https://img.shields.io/endpoint?url=https://dashboard.cypress.io/badge/detailed/q1jdu2&style=flat&logo=cypress)](https://dashboard.cypress.io/projects/q1jdu2/runs)
[![Contributors](https://img.shields.io/github/contributors/apache/streampipes)](https://github.com/apache/streampipes/graphs/contributors)
![GitHub commit activity](https://img.shields.io/github/commit-activity/y/apache/streampipes)

<h1 align="center">
  <br>
   <img src="https://streampipes.apache.org/img/sp-logo-color.png" 
   alt="StreamPipes Logo" title="Apache StreamPipes Logo" width="50%"/>
  <br>
</h1>
<h3 align="center">Self-Service Data Analytics for the (Industrial) IoT</h3>
<h4 align="center">StreamPipes is a self-service (Industrial) IoT toolbox to enable non-technical users to connect
, analyze and explore IoT data streams. </h4>
<p align="center">  
    <img src="https://raw.githubusercontent.com/apache/streampipes/dev/images/streampipes-overview.png" alt="StreamPipes Overview"/>
</p>


***

## Table of contents

  * [About Apache StreamPipes](#about-apache-streampipes)
  * [User interface](#userinterface)
  * [Installation](#installation)
  * [Documentation](#documentation)
  * [Building StreamPipes](#building-streampipes)
  * [Pipeline Elements](#pipeline-elements)  
  * [Extending StreamPipes](#extending-streampipes)
  * [Bugs and Feature Requests](#bugs-and-feature-requests)
  * [Get help](#get-help)
  * [Contribute](#contribute)
  * [Feedback](#feedback)
  * [License](#license)

***

## About Apache StreamPipes

Apache StreamPipes makes industrial data analytics easy!

StreamPipes is an end-to-end toolbox for the industrial IoT. 
It comes with a rich graphical user interface targeted at non-technical users and provides the following features:


* Quickly connect >20 industrial protocols such as OPC-UA, PLCs, MQTT, REST, Pulsar, Kafka and others.
* Create data harmonization and analytics pipelines using > 100 algorithms and data sinks to forward data to third-party systems.
* Use the data explorer to visually explore historical data with many widgets tailored for time-series data.
* A live dashboard to display real-time data from data sources and pipelines, e.g., for shopfloor monitoring.


StreamPipes is highly extensible and includes a Java SDK to create new 
pipeline elements and adapters. Python support is available in an early development stage - stay tuned!  
Pipeline elements are standalone microservices that can run anywhere -
centrally on your server or close at the edge.
You want to employ your own machine learning model on live data?
Just write your own data processor and make it reusable as a pipeline element.

Besides that, StreamPipes includes features for production deployments:

* Assign resources such as pipelines, data streams and dashboards to assets for better organization
* Monitoring & metrics of pipelines and adapters
* Built-in user and access rights management
* Export and import resources

## User interface

* Connect data from an OPC-UA server following a three-step configuration process:

![StreamPipes Connect](https://raw.githubusercontent.com/apache/streampipes/dev/images/streampipes-connect.gif)

* Create a pipeline to detect a continuous decrease using a trend detection data processor and a ``Notification``sink:

![StreamPipes Pipeline Editor](https://raw.githubusercontent.com/apache/streampipes/dev/images/streampipes-pipelines.gif)

* Visually analyze data using the data explorer:

![StreamPipes Data Explorer](https://raw.githubusercontent.com/apache/streampipes/dev/images/streampipes-data-explorer.gif)


## Installation

The quickest way to run StreamPipes including the latest extensions (adapters, pipeline elements) is by using our Docker-based [installation & operation options](installer), namely: 

* **[StreamPipes Compose](installer/compose)** - The User's Choice
* **[StreamPipes CLI](installer/cli)** - The Developer's Favorite
* **[StreamPipes k8s](installer/k8s)** - The Operator's Dream

> **NOTE**: StreamPipes CLI & k8s are highly recommended for developers or operators. Standard users should stick to StreamPipes Compose.

Please follow the instructions provided in the corresponding `README.md` to get started.

For a more in-depth manual, read the [installation guide](https://streampipes.apache.org/docs/docs/try-installation.html).

> TL;DR: Download the latest release, switch to the ``installer/compose`` directory and run ``docker-compose up -d``.

## Documentation

The full documentation is available [here](https://streampipes.apache.org/docs/index.html).

Quick Links:

* [Installation](https://streampipes.apache.org/docs/docs/try-installation.html)
* [Create adapters](https://streampipes.apache.org/docs/docs/use-connect.html)
* [Create pipelines](https://streampipes.apache.org/docs/docs/use-pipeline-editor.html)
* [Pipeline elements](https://streampipes.apache.org/pipeline-elements.html)
* [Write you own pipeline elements](https://streampipes.apache.org/docs/docs/extend-archetypes.html)

## Building StreamPipes

To properly build the StreamPipes core, the following tools should be installed:

### Prerequisites
* Java 11 JDK (minimum)
* Maven (tested with 3.8)
* NodeJS + NPM (tested with v12+/ v6+)
* Docker + Docker-Compose

### Building

To build the core project, do the following:

```
    mvn clean package
```

To build the ui, switch to the ``ui`` folder and perform the following steps:

```
    npm install
    # for NPM > v7, run npm install --legacy-peer-deps
    npm run build
```

### Starting

To start StreamPipes, run ``docker-compose up --build -d`` from the root directory.

You can also use the installer or CLI as described in the ``Installation`` section.

## Pipeline Elements
StreamPipes includes a repository of extensions for adapters and pipeline elements:
* **Connect adapters** for a variety of IoT data sources as well as 
* **Data Processors** and **Data Sinks** as ready-to-use pipeline elements. 

The source code of all included pipeline elements and adapters can be found [here](https://github.com/apache/streampipes/tree/dev/streampipes-extensions).

## Extending StreamPipes

You can easily add your own data streams, processors or sinks. A [Java-based SDK](https://streampipes.apache.org/docs/docs/extend-tutorial-data-processors.html) can be used to integrate your existing processing logic into StreamPipes. 
Pipeline elements are packaged as Docker images and can be installed at runtime, whenever your requirements change.

👉 Check our developer guide at [https://streampipes.apache.org/docs/docs/extend-setup.html](https://streampipes.apache.org/docs/docs/extend-setup.html).

## Bugs and Feature Requests

If you've found a bug or have a feature that you'd love to see in StreamPipes, feel free to create an issue on GitHub:

👉 [Bugs](https://github.com/apache/streampipes/issues)
👉 [Feature requests](https://github.com/apache/streampipes/discussions/categories/ideas)

## Get help

If you have any problems during the installation or questions around StreamPipes, you'll get help through one of our 
community channels:

👉 [Mailing Lists](https://streampipes.apache.org/mailinglists.html)

Or directly subscribe to [users-subscribe@streampipes.apache.org](mailto:users-subscribe@streampipes.apache.org)!

👉 And don't forget to follow us on [Twitter](https://twitter.com/streampipes)!

## Contribute

We welcome all kinds of contributions to StreamPipes. If you are interested in contributing, let us know! You'll
 get to know an open-minded and motivated team working together to build the next IIoT analytics toolbox.

Here are some first steps in case you want to contribute:
* Subscribe to our dev mailing list [dev-subscribe@streampipes.apache.org](dev-subscribe@streampipes.apache.org)
* Send an email, tell us about your interests and which parts of StreamPipes you'd like to contribute (e.g., core or UI)!
* Ask for a mentor who helps you to understand the code base and guides you through the first setup steps
* Find an issue on [GitHub](https://github.com/apache/streampipes/issues) which is tagged with a _good first issue_ tag
* Have a look at our developer wiki at [https://cwiki.apache.org/confluence/display/STREAMPIPES](https://cwiki.apache.org/confluence/display/STREAMPIPES) to learn more about StreamPipes development.

Have fun!

## Feedback

We'd love to hear your feedback! Subscribe to [users@streampipes.apache.org](mailto:users@streampipes.apache.org)

## License

[Apache License 2.0](LICENSE)
