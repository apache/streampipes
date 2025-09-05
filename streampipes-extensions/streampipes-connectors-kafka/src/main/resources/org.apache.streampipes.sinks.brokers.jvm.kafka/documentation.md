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

## Kafka Publisher

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description
The Kafka Publisher sink enables StreamPipes to publish events to Apache Kafka topics. It provides:
* Real-time event publishing to Kafka topics
* Support for various security configurations
* Automatic topic creation if not exists
* Configurable message handling
* JSON message serialization

***

## Required Input
This sink accepts any incoming event type and serializes it to JSON format before publishing to Kafka.

***

## Configuration

### Broker Settings
* **Broker Hostname**: The hostname or IP address of the Kafka broker (e.g., test.server.com). Do not include the protocol.
* **Broker Port**: The port number of the Kafka broker (default: 9092)

### Security Settings
* **Security Protocol**: Choose the security protocol for broker communication:
  * `PLAINTEXT`: No authentication and plaintext communication
  * `SSL`: Using SSL with no authentication
  * `SASL/PLAINTEXT`: SASL authentication without encryption
  * `SASL/SSL`: SASL authentication with SSL encryption

* **Authentication** (when using SASL):
  * **Security Mechanism**: Choose the SASL mechanism:
    * `PLAIN`: Simple username/password authentication
    * `SCRAM-SHA-256`: SCRAM authentication with SHA-256
    * `SCRAM-SHA-512`: SCRAM authentication with SHA-512
  * **Username**: SASL authentication username
  * **Password**: SASL authentication password

### Topic Settings
* **Topic**: The Kafka topic where events will be published. If the topic doesn't exist, it will be created automatically with default settings.

### Advanced Settings
* **Additional Configurations**: Add custom Kafka producer configurations in key=value format. Each configuration should be on a new line. For example:
  ```
  buffer.memory=33554432
  batch.size=16384
  linger.ms=20
  ```

***

## Features
* **Message Handling**:
  * Automatic JSON serialization of events
  * Configurable message size limits
  * Batch processing support
  * Automatic topic creation

* **Security**:
  * SSL/TLS encryption support
  * SASL authentication with multiple mechanisms
  * Configurable security protocols

***

## Use Cases
* **Data Distribution**: Publish processed events to Kafka for other systems to consume
* **Event Streaming**: Stream events to Kafka for real-time processing
* **Data Integration**: Integrate StreamPipes with Kafka-based data pipelines

***

## Important Notes
* The sink uses the Kafka Producer API to publish messages
* Events are automatically serialized to JSON format
* Topics are created automatically if they don't exist
* For production use, it's recommended to configure appropriate security settings
* The sink supports all standard Kafka producer configurations through the additional settings
