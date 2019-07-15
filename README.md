# StreamPipes

StreamPipes enables flexible modeling of stream processing pipelines by providing a graphical modeling editor on top of existing stream processing frameworks.

It leverages non-technical users to quickly define and execute processing pipelines based on an easily extensible 
toolbox of data sources, data processors and data sinks.

Learn more about StreamPipes at [https://www.streampipes.org/](https://www.streampipes.org/)

Read the full documentation at [https://docs.streampipes.org](https://docs.streampipes.org)

### StreamPipes Pipeline Elements

This project provides a library of several pipeline elements that can be used within the StreamPipes toolbox.

Currently, the following pipeline elements are available:

**Data Sources**
* Watertank Simulator: A data simulator that replays data from an Industrial IoT use case. Several streams are provided such as flow rate, water level and more.
* Vehicle Simulator: Provides a simulated stream that replays location-based real-time data of a vehicle.
* Random Data Generator: Several streams that produce randomly generated data in an endless stream.

**Data Processors**
* Aggregation
* Count Aggregation
* Event Rate
* Timestamp Enricher
* Numerical Filter: Filters sensor values based on a configurable threshold value.
* Text Filter: Filters text-based fields by a given string value.
* Projection: Outputs a configurable subset of the fields available in an input event stream.
* Geocoding
* Google Routing
* Increase
* Peak Detection
* Statistics Summary
* Statistics Summary Window-Based
* Field Converter
* Field Hasher
* Field Mapper
* Measurement Unit Converter
* Field Renamer


**Data Sinks**
* CouchDB: Stores events in an Apache CouchDB database.
* Dashboard: Can be used to display pipeline results in the real-time dashboard of the StreamPipes UI.
* Kafka Publisher: Publishes events to an Apache Kafka broker.
* Notification: Can be used to generate notifications that are shown in the notification center of the StreamPipes UI.
* JMS
* RabbitMQ
* Elasticsearch
* Dashboard
* Notification
* Email Notification
* Slack Notification
* OneSignal Notification

Contact us if you are missing some pipeline elements!

### Getting started

All modules contain a docker-compose template in the `deployment` folder of each module. Copy this into your StreamPipes docker-compose file (see the docs for more info) and start StreamPipes! All pipeline elements are automatically registered (but do not forget to install them under `Pipeline Element Installation`).

It's easy to get started:
* Download the installer script from [https://github.com/streampipes/streampipes-installer](https://github.com/streampipes/streampipes-installer)
* Follow the installation guide at [https://docs.streampipes.org/docs/user-guide-installation](https://docs.streampipes.org/docs/user-guide-installation)
* Check the [tour](https://docs.streampipes.org/docs/user-guide-tour) and build your first pipeline!

### Feedback

We'd love to hear your feedback! Contact us at [feedback@streampipes.org](mailto:feedback@streampipes.org)

