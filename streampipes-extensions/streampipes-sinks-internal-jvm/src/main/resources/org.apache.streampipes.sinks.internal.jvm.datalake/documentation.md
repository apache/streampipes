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

Stores events in the internal data lake so that data can be visualized in the live dashboard or in the data explorer.
Simply create a pipeline with a data lake sink, switch to one of the data exploration tool and start exploring your
data!

***

## Required input

This sink requires an event that provides a timestamp value (a field that is marked to be of type ``http://schema
.org/DateTime``.

***

## Configuration

### Identifier

The name of the measurement (table) where the events are stored.

### Schema Update Options

The Schema Update Options dictate the behavior when encountering a measurement (table) with the same identifier.

#### Option 1: Update Schema

- **Description:** Overrides the existing schema.
- **Effect on Data:** The data remains in the data lake, but accessing old data is restricted to file export.
- **Impact on Features:** Other StreamPipes features, such as the Data Explorer, will only display the new event schema.

#### Option 2: Extend Existing Schema

- **Description:** Keeps old event fields in the event schema.
- **Strategy:** This follows an append-only strategy, allowing continued work with historic data.
- **Consideration:** Old properties may exist for which no new data is generated.
