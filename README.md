[![Codacy Badge](https://api.codacy.com/project/badge/Grade/34a7e26be4fc4fa284ee5201b6d386ea)](https://www.codacy.com/app/dominikriemer/streampipes-ce?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=streampipes/streampipes-ce&amp;utm_campaign=Badge_Grade)
[![Docker pulls](https://img.shields.io/docker/pulls/streampipes/backend.svg)](https://hub.docker.com/r/streampipes/backend/)
![Maven central](https://img.shields.io/maven-central/v/org.streampipes/streampipes-backend.svg)
[![License](https://img.shields.io/github/license/streampipes/streampipes-ce.svg)](https://docs.streampipes.org/license/)
[![License](https://img.shields.io/github/last-commit/streampipes/streampipes-ce.svg)]()
<h1 align="center">
  <br>
   <img src="https://docs.streampipes.org/img/logo.png" 
   alt="StreamPipes Logo" title="StreamPipes Logo" width="50%"/>
  <br>
</h1>
<h3 align="center">Self-Service Data Analytics for the (Industrial) IoT</h3>
<h4 align="center">StreamPipes is a complete toolbox to easily analyze IoT (big) data streams without programming skills.</h4>
<p align="center">  
    <img src="https://www.streampipes.org/images/screenshot.png" alt="StreamPipes Pipeline Editor"/>
</p>

***

## Table of contents

  * [About StreamPipes](#about-streampipes)
  * [Use Cases](#use-cases)
  * [Installation](#installation)
  * [Pipeline Elements](#pipeline-elements)
  * [Extending StreamPipes](#extending-streampipes)
  * [Get help](#get-help)
  * [Contribute](#contribute)
  * [Feedback](#feedback)
  * [License](#license)

***

## About StreamPipes

StreamPipes enables flexible modeling of stream processing pipelines by providing a graphical modeling editor on top of existing stream processing frameworks.

It leverages non-technical users to quickly define and execute processing pipelines based on an easily extensible 
toolbox of data sources, data processors and data sinks. StreamPipes has an exchangeable runtime execution layer and executes pipelines using one of the provided wrappers, e.g., for Apache Flink or Apache Kafka Streams.

Pipeline elements in StreamPipes can be installed at runtime - the built-in SDK allows to easily implement new 
pipeline elements according to your needs. Pipeline elements are standalone microservices that can run anywhere - centrally on your server, in a large-scale cluster or close at the edge.

Learn more about StreamPipes at [https://www.streampipes.org/](https://www.streampipes.org/)

Read the full documentation at [https://docs.streampipes.org](https://docs.streampipes.org)

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
2. Clone or download the installer script from [https://www.github.com/streampipes/streampipes-installer](https://www.github.com/streampipes/streampipes-installer)
3. Execute ``./streampipes start`` to run a lightweight StreamPipes version with few pipelines elements (not including Big Data frameworks) or start the full version (16GB RAM recommended) by executing ``./streampipes start bigdata`` 
4. Open your browser, navigate to ``http://YOUR_HOSTNAME_HERE`` and follow the installation instructions.
5. Once finished, switch to the pipeline editor and start the interactive tour or check the [online tour](https://docs.streampipes.org/docs/user-guide-tour) to learn how to create your first pipeline!

For a more in-depth manual, read the installation guide at [https://docs.streampipes.org/docs/user-guide-installation](https://docs.streampipes.org/docs/user-guide-installation)!

## Pipeline Elements

StreamPipes includes a repository of ready-to-use pipeline elements. A description of the standard elements can be 
found in the Github repository [streampipes-pipeline-elements](https://www.github.com/streampipes/streampipes-pipeline-elements).

## Extending StreamPipes

You can easily add your own data streams, processors or sinks. A [Java-based SDK](https://docs.streampipes.org/docs/dev-guide-tutorial-processors) and several [run-time wrappers](https://docs.streampipes.org/docs/dev-guide-architecture) for popular streaming frameworks such as Apache Flink, Apache Spark and Apache 
Kafka Streams (and also plain Java programs) can be used to integrate your existing processing logic into StreamPipes. Pipeline elements are packaged as Docker images and can be installed at runtime, whenever your requirements change.

Check our developer guide at [https://docs.streampipes.org/docs/dev-guide-introduction](https://docs.streampipes.org/docs/dev-guide-introduction).

## Get help

If you have any problems during the installation or questions around StreamPipes, you'll get help through one of our 
community channels:

- [Slack](https://slack.streampipes.org)
- [E-Mail](mailto:feedback@streampipes.org)

And don't forget to follow us on [Twitter](https://twitter.com/streampipes)!

## Contribute

We welcome contributions to StreamPipes. If you are interested in contributing to StreamPipes, let us know!

## Feedback

We'd love to hear your feedback! Contact us at [feedback@streampipes.org](mailto:feedback@streampipes.org)

## License

[Apache License 2.0](LICENSE)

StreamPipes is actively being developed by a dedicated group of people at [FZI Research Center for Information Technology](https://www.fzi.de).