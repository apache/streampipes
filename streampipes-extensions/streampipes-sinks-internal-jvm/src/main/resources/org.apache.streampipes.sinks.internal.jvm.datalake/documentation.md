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

### Dimensions

The fields which will be stored as dimensional values in the time series storage. Dimensions are typically identifiers 
such as the ID of a sensor.
Dimensions support grouping in the data explorer, but will be converted to a text-based field and provide less advanced 
filtering capabilities.

Be careful when modifying dimensions of existing pipelines! This might have impact on how you are able to view data in 
the data explorer due to schema incompatibilities.

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


### Dimensions

Select fields which will be marked as dimensions. Dimensions reflect tags in the underlying time-series database. 
Dimensions support grouping operations and can be used for fields with a limited set of values, e.g., boolean flags or 
fields representing IDs. Dimensions are not a good choice for fields with a high number of different values since they 
slow down database queries.

By default, all fields which are marked as dimensions in the metadata are chosen and can be manually overridden 
with this configuration.

Data types which can be marked as dimensional values are booleans, integer, and strings.

### Ignore Duplicates

Before writing an event to the time series storage, a duplicate check is performed. By activating this option, only 
fields having a different value than the previous event are stored. 
This setting only affects measurement fields, not dimensions.
