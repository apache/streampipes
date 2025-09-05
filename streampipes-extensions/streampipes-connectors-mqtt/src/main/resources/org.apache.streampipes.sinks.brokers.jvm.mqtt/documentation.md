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

## MQTT Publisher

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description
The MQTT Publisher sink enables StreamPipes to publish events to MQTT topics. It provides:
* Real-time event publishing to MQTT topics
* Support for various authentication methods
* Configurable message handling
* Last Will and Testament support
* MQTT 3.1 and 3.1.1 protocol support

***

## Configuration

### Broker Settings
* **Host**: The hostname or IP address of the MQTT broker
* **Port**: The port number of the MQTT broker (default: 1883)

### Topic Settings
* **Topic**: The MQTT topic where events will be published

### Authentication Settings
* **Authentication**: Choose between:
  * **Unauthenticated**: No authentication required
  * **Basic Auth**: Username/password authentication
    * **Username**: The username for authentication
    * **Password**: The password for authentication

### Message Settings
* **QoS Level**: Quality of Service level for message delivery (default: at-least-once)
* **Clean Session**: Whether to start with a clean session (default: Yes)
* **Retain Messages**: Whether to retain messages at the broker (default: No)
* **Keep Alive**: Time in seconds between keep-alive messages (default: 30s)

### Last Will and Testament (Optional)
* **Last Will Mode**: Choose between:
  * **No Last Will**: No last will message
  * **Last Will**: Configure a last will message
    * **Last Will Topic**: The topic for the last will message
    * **Last Will Message**: The message to publish
    * **Last Will QoS**: QoS level for the last will message
    * **Retain Will Message**: Whether to retain the last will message

### Advanced Settings
* **MQTT Version**: Whether the broker is MQTT v3.1.1 compliant (default: Yes)
* **Reconnect Period**: Time in seconds between reconnect attempts (default: 30s)

***

## Features
* **Message Handling**:
  * Configurable QoS levels
  * Message retention options
  * Clean session support
  * Last Will and Testament

* **Security**:
  * Basic authentication support
  * TCP protocol support
  * SSL/TLS support (coming soon)

* **Protocol Support**:
  * MQTT 3.1
  * MQTT 3.1.1

***

## Use Cases
* **IoT Data Publishing**: Publish processed data to IoT devices
* **Real-time Updates**: Send real-time updates to MQTT subscribers
* **Device Control**: Send control commands to MQTT-enabled devices
* **System Integration**: Integrate with MQTT-based systems

***

## Important Notes
* The sink uses the MQTT protocol for message publishing
* Events are published in real-time as they are received
* The sink automatically handles reconnection if the connection to the broker is lost
* For production use, it's recommended to use authentication when available
* The sink supports both MQTT 3.1 and 3.1.1 protocols
* Last Will and Testament can be used to notify other clients of unexpected disconnections

## Required input

This sink does not have any requirements and works with any incoming event type.
