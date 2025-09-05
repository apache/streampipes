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

## Apache Kafka Adapter

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description
The Apache Kafka protocol adapter enables StreamPipes to connect to Kafka brokers and consume messages. It provides:
* Real-time message consumption from Kafka topics
* Dynamic topic selection with runtime resolution
* Support for both simple and wildcard topic subscriptions
* Configurable consumer group settings
* Multiple security options including SSL/TLS and SASL authentication
* Configurable message handling and offset management

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
* **Topic**: Select a Kafka topic to subscribe to. The list of available topics is dynamically loaded from the broker.
* **Hide Internal Topics**: Option to hide topics that are only used internally by Kafka (e.g., topics starting with `__`)

### Consumer Settings
* **Consumer Group**: Configure how to handle consumer group ID:
  * `Random group id`: StreamPipes generates a random group ID (recommended for testing)
  * `Custom group id`: Specify a custom group ID for production use

* **Auto Offset Reset**: Configure where to start reading messages:
  * `Earliest`: Start reading from the earliest available messages
  * `Latest`: Start reading from the latest messages
  * `None`: Throw exceptions if no offset is available

* **Additional Configurations**: Add custom Kafka consumer configurations in key=value format. Each configuration should be on a new line.

### Advanced Settings
* **Message Max Bytes**: Maximum size of messages to consume (default: 52428800 bytes)
* **Session Timeout**: Consumer session timeout in milliseconds (default: 30000)
* **Auto Commit Interval**: Interval for committing offsets in milliseconds (default: 5000)