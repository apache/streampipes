# StreamPipes

StreamPipes enables flexible modeling of stream processing pipelines by providing a graphical modeling editor on top of existing stream processing frameworks.

It leverages non-technical users to quickly define and execute processing pipelines based on an easily extensible 
toolbox of data sources, data processors and data sinks.

Learn more about StreamPipes at [https://www.streampipes.org/](https://www.streampipes.org/)

Read the full documentation at [https://docs.streampipes.org](https://docs.streampipes.org)

### StreamPipes examples for standalone pipeline elements running directly on the JVM

This project includes examples for StreamPipes data processors and data sinks that do not use a specific runtime such
 as Apache Flink but run directly on the JVM in a single-host manner. These components are suitable for processing 
 event streams with rather low frequency (e.g., up to a few thousand events per second)

Currently, the following example pipeline elements are available:

**Data Processors**
* Numerical Filter: Filters sensor values based on a configurable threshold value.
* Text Filter: Filters text-based fields by a given string value.
* Projection: Outputs a configurable subset of the fields available in an input event stream.

**Data Sinks**
* CouchDB: Stores events in an Apache CouchDB database.
* Dashboard: Can be used to display pipeline results in the real-time dashboard of the StreamPipes UI.
* Kafka Publisher: Publishes events to an Apache Kafka broker.
* Notification: Can be used to generate notifications that are shown in the notification center of the StreamPipes UI.

### Getting started

Currently, the StreamPipes core is available as a preview in form of ready-to-use Docker images.

It's easy to get started:
* Download the `docker-compose.yml` file from [https://www.github.com/streampipes/preview-docker](https://www.github.com/streampipes/preview-docker)
* Follow the installation guide at [https://docs.streampipes.org/quick_start/installation](https://docs.streampipes.org/quick_start/installation)
* Check the [tour](https://docs.streampipes.org/user_guide/features) and build your first pipeline!

### Extending StreamPipes

You can easily add your own data streams, processors or sinks. 

Check our developer guide at [https://docs.streampipes.org/developer_guide/introduction](https://docs.streampipes.org/developer_guide/introduction)

### Feedback

We'd love to hear your feedback! Contact us at [mail.streampipes@gmail.com](mailto:mail.streampipes@gmail.com)

