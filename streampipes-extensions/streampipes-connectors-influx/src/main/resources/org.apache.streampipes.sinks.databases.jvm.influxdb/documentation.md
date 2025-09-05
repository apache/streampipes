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

## InfluxDB

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

Stores events in an InfluxDB.

***

## Required input

This sink requires an event that provides a timestamp value (a field that is marked to be of type ``http://schema
.org/DateTime``.

***

## Configuration

### Hostname

The hostname/URL of the InfluxDB instance. (Include http(s)://).

### Port

The port of the InfluxDB instance.

### Database Name

The name of the database where events will be stored.

### Measurement Name

The name of the Measurement where events will be stored (will be created if it does not exist).

### Username

The username for the InfluxDB Server.

### Password

The password for the InfluxDB Server.

### Timestamp Field

The field which contains the required timestamp.

### Buffer Size

Indicates how many events are written into a buffer, before they are written to the database.

### Maximum Flush

The maximum waiting time for the buffer to fill the Buffer size before it will be written to the database in ms.
## Output

(not applicable for data sinks)
