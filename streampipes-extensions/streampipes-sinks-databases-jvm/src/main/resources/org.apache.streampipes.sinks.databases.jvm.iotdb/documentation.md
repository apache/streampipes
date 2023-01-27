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

## IoTDB

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

Stores events in a IoTDB database.

Events will be stored in timeseries `root.${Database Name}.${Device (Entity) Name}.${Event Key}`.

Please reference to [https://iotdb.apache.org/](https://iotdb.apache.org/) for more information.

***

## Required input

This sink does not have any requirements and works with any incoming event type.

***

## Configuration

### Hostname

The hostname of the IoTDB instance.

### Port

The port of the IoTDB instance (default 6667).

### Username

The username for the IoTDB Server.

### Password

The password for the IoTDB Server.

### **Database Name**

The name of the database where events will be stored (will be created if it does not exist).

A database is a group of devices (entities). Users can create any prefix path as a database.

### **Device (Entity) Name**

The name of the device (entity) where events will be stored.

A device (also called entity) is an equipped with measurements in real scenarios. In IoTDB, all measurements should have
their corresponding devices.

### **Measurements**

All keys of fields in an event will be automatically converted to suffixes of timeseries.

A measurement is information measured by detection equipment in an actual scene and can transform the sensed information
into an electrical signal or other desired form of information output and send it to IoTDB.

In IoTDB, all data and paths stored are organized in units of measurements.

## Output

(not applicable for data sinks)
