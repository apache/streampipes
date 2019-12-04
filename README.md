<!--

  Copyright 2018 FZI Forschungszentrum Informatik

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.

-->

[![Travis Badge](https://travis-ci.org/apache/incubator-streampipes.svg?branch=dev)](https://travis-ci.org/apache/incubator-streampipes.svg?branch=dev)
[![Docker pulls](https://img.shields.io/docker/pulls/streampipes/backend.svg)](https://hub.docker.com/r/streampipes/backend/)
[![Maven central](https://img.shields.io/maven-central/v/org.streampipes/streampipes-backend.svg)](https://img.shields.io/maven-central/v/org.streampipes/streampipes-backend.svg)
[![License](https://img.shields.io/github/license/apache/incubator-streampipes.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![Last commit](https://img.shields.io/github/last-commit/apache/incubator-streampipes.svg)]()
[![Twitter](https://img.shields.io/twitter/follow/StreamPipes.svg?label=Follow&style=social)](https://twitter.com/StreamPipes)

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
    <img src="https://streampipes.apache.org/img/screenshots/pipeline-editor-2.png" alt="StreamPipes Pipeline
     Editor"/>
</p>

***

## Table of contents

  * [About Apache StreamPipes](#about-streampipes)
  * [Use Cases](#use-cases)
  * [Installation](#installation)
  * [Pipeline Elements](#pipeline-elements)
  * [Extending StreamPipes](#extending-streampipes)
  * [Get help](#get-help)
  * [Contribute](#contribute)
  * [Feedback](#feedback)
  * [License](#license)

***

## About Apache StreamPipes

Apache StreamPipes (incubating) enables flexible modeling of stream processing pipelines by providing a graphical
 modeling
 editor on top of existing stream processing frameworks.

It leverages non-technical users to quickly define and execute processing pipelines based on an easily extensible 
toolbox of data sources, data processors and data sinks. StreamPipes has an exchangeable runtime execution layer and executes pipelines using one of the provided wrappers, e.g., for Apache Flink or Apache Kafka Streams.

Pipeline elements in StreamPipes can be installed at runtime - the built-in SDK allows to easily implement new 
pipeline elements according to your needs. Pipeline elements are standalone microservices that can run anywhere - centrally on your server, in a large-scale cluster or close at the edge.

Learn more about StreamPipes at [https://streampipes.apache.org/](https://streampipes.apache.org/)

Read the full documentation at [https://streampipes.apache.org/docs](https://streampipes.apache.org/docs)

## Use Cases

StreamPipes allows you to connect IoT data sources using the SDK or the built-in graphical tool StreamPipes 
Connect.

The extensible toolbox of data processors and sinks supports use cases such as
* Continuously **store** IoT data streams to third party systems (e.g., databases)
* **Filter** measurements on streams (e.g., based on thresholds or value ranges)
* **Harmonize** data by using data processors for transformations (e.g., by converting measurement units and data types
 or by aggregating measurements)
* **Detect situations** that should be avoided (e.g., patterns based on time windows)
* Wrap **Machine Learning models** into data processors to perform classifications or predictions on sensor and image data
* **Visualize** real-time data from sensors and machines using the built-in Live Dashboard


## Installation

The quickest way to run StreamPipes is the Docker-based installer script available for Unix, Mac and Windows (10).

It's easy to get started:
1. Make sure you have [Docker](https://docs.docker.com/install/) and [Docker Compose](https://docs.docker.com/compose/install/) installed.
2. Clone or download the installer script from [https://www.github.com/apache/incubator-streampipes-installer](https://www.github.com/apache/incubator-streampipes-installer)
3. Execute ``./streampipes start`` 
4. Enter the hostname and choose the version you'd like to run (the Lite version runs with less memory assigned to Docker (< 6 GB), use the full version if you have more memory available)
5. Open your browser, navigate to ``http://YOUR_HOSTNAME_HERE`` and follow the installation instructions.
6. Once finished, switch to the pipeline editor and start the interactive tour or check the [online tour](https://streampipes.apache.org/docs/docs/user-guide-tour/) to learn how to create your first pipeline!

For a more in-depth manual, read the installation guide at [https://streampipes.apache.org/docs/docs/user-guide-installation/](https://streampipes.apache.org/docs/docs/user-guide-installation/)!

## Pipeline Elements

StreamPipes includes a repository of ready-to-use pipeline elements. A description of the standard elements can be 
found in the Github repository [streampipes-extensions](https://www.github.com/apache/incubator-streampipes-extensions).

## Extending StreamPipes

You can easily add your own data streams, processors or sinks. A [Java-based SDK](https://streampipes.apache.org/docs/docs/dev-guide-tutorial-processors) and several [run-time wrappers](https://streampipes.apache.org/docs/docs/dev-guide-architecture) for popular streaming frameworks such as Apache Flink, Apache Spark and Apache 
Kafka Streams (and also plain Java programs) can be used to integrate your existing processing logic into StreamPipes. Pipeline elements are packaged as Docker images and can be installed at runtime, whenever your requirements change.

Check our developer guide at [https://streampipes.apache.org/docs/docs/dev-guide-introduction](https://streampipes.apache.org/docs/docs/dev-guide-introduction).

## Get help

If you have any problems during the installation or questions around StreamPipes, you'll get help through one of our 
community channels:

- [Slack](https://slack.streampipes.org)
- [Mailing Lists](https://streampipes.apache.org/mailinglists.html)

And don't forget to follow us on [Twitter](https://twitter.com/streampipes)!

## Contribute

We welcome contributions to StreamPipes. If you are interested in contributing to StreamPipes, let us know!

## Feedback

We'd love to hear your feedback! Subscribe to [users@streampipes.apache.org](mailto:users@streampipes.apache.org)

## License

[Apache License 2.0](LICENSE)
