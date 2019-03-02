<img src="https://www.streampipes.org/images/screenshot.png" alt="StreamPipes Pipeline Editor"/>

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/34a7e26be4fc4fa284ee5201b6d386ea)](https://www.codacy.com/app/dominikriemer/streampipes-ce?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=streampipes/streampipes-ce&amp;utm_campaign=Badge_Grade)
[![Docker pulls](https://img.shields.io/docker/pulls/streampipes/backend.svg)](https://hub.docker.com/r/streampipes/backend/)
[![License](https://img.shields.io/github/license/streampipes/streampipes-ce.svg)](https://docs.streampipes.org/license/)
[![License](https://img.shields.io/github/last-commit/streampipes/streampipes-ce.svg)]()

# StreamPipes


StreamPipes enables flexible modeling of stream processing pipelines by providing a graphical modeling editor on top of existing stream processing frameworks.

It leverages non-technical users to quickly define and execute processing pipelines based on an easily extensible 
toolbox of data sources, data processors and data sinks. StreamPipes has an exchangeable runtime execution layer and executes pipelines using one of the provided wrappers, e.g., for Apache Flink or Apache Kafka Streams.

Learn more about StreamPipes at [https://www.streampipes.org/](https://www.streampipes.org/)

Read the full documentation at [https://docs.streampipes.org](https://docs.streampipes.org)

### Use Cases

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


### Getting started

It's easy to get started:
* Clone the installer script from [https://www.github.com/streampipes/streampipes-installer](https://www.github.com/streampipes/streampipes-installer)
* Follow the installation guide at [https://docs.streampipes.org/docs/user-guide-installation](https://docs.streampipes.org/docs/user-guide-installation)
* Check the [tour](https://docs.streampipes.org/docs/user-guide-tour) and build your first pipeline!

### Extending StreamPipes

You can easily add your own data streams, processors or sinks. 

Check our developer guide at [https://docs.streampipes.org/docs/dev-guide-introduction](https://docs.streampipes.org/docs/dev-guide-introduction)

### Community

- [Twitter](https://twitter.com/streampipes)
- [Email](mailto:feedback@streampipes.org)

### Contributing

We welcome contributions to StreamPipes. If you are interested in contributing to StreamPipes, let us know!

### Feedback

We'd love to hear your feedback! Contact us at [feedback@streampipes.org](mailto:feedback@streampipes.org)

