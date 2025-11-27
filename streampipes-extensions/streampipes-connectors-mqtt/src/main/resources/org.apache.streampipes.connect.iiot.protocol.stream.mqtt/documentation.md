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

## MQTT Protocol

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description
The MQTT protocol adapter enables StreamPipes to consume messages from an MQTT broker. It provides:
* Real-time message consumption from MQTT topics
* Support for various authentication methods
* Configurable message handling
* Automatic reconnection handling
* Support for MQTT 3.1 and 3.1.1 protocols

***

## Configuration

### Broker Settings
* **Broker URL**: The URL of the MQTT broker (e.g., tcp://test-server.com:1883 for tcp or ssl://test-server.com:8883 for TLS). The protocol (tcp://) or (ssl://) and port are required.

### Topic Settings
* **Topic**: The MQTT topic to subscribe to (e.g., test/topic)

### Authentication Settings
* **Access Mode**: Choose between:
  * **Unauthenticated**: No authentication required
  * **Username/Password**: Basic authentication with username and password
    * **Username**: The username for authentication
    * **Password**: The password for authentication
  * **Client Certificate** 
    * **Certificate PEM**: Public Key in PEM format
    * **Private Key PEM**: Private (RSA) Key in PEM format (without password !)

***

## Features
* **Message Handling**:
  * Real-time message consumption
  * Support for MQTT QoS levels
  * Automatic reconnection on connection loss
  * Configurable keep-alive settings

* **Security**:
  * Basic authentication support
  * TCP protocol support
  * SSL/TLS support

* **Protocol Support**:
  * MQTT 3.1
  * MQTT 3.1.1

***

## Use Cases
* **IoT Data Collection**: Connect to IoT devices publishing data via MQTT
* **Sensor Networks**: Subscribe to sensor data streams
* **Real-time Monitoring**: Monitor real-time data from MQTT-enabled systems
* **Device Integration**: Integrate with MQTT-based device ecosystems

***

## Important Notes
* The adapter uses the MQTT protocol for message consumption
* Messages are received in real-time as they are published to the subscribed topic
* The adapter automatically handles reconnection if the connection to the broker is lost
* For production use, it's recommended to use authentication when available
* The adapter supports both MQTT 3.1 and 3.1.1 protocols

## Output

