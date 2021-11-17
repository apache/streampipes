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

## Nats Publisher

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

Publishes events to NATS broker.

***

## Required input

This sink does not have any requirements and works with any incoming event type.

***

## Configuration

### NATS Subject

The subject (topic) where events should be sent to.

### NATS Broker URL

The URL to connect to the NATS broker. It can be provided multiple urls separated by commas(,).
 (e.g., nats://localhost:4222,nats://localhost:4223)
 
### Username

The username to authenticate the client with NATS broker.

It is an optional configuration.  

### NATS Broker URL

The password to authenticate the client with NATS broker. 

It is an optional configuration.

### NATS Connection Properties

All other possible connection configurations that the nats client can be created with.
It can be provided as key value pairs separated by colons(:) and commas(,).
 (e.g., io.nats.client.reconnect.max:1, io.nats.client.timeout:1000)

It is an optional configuration.

## Output

(not applicable for data sinks)