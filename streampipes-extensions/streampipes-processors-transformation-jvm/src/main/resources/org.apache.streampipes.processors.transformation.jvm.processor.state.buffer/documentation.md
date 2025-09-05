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

## State Buffer

<p align="center">
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

The State Buffer processor caches sensor values during specific states. It supports:
* State-based value buffering
* Timestamp tracking
* Sensor value caching
* State monitoring

This processor is essential for:
* Caching sensor values
* Tracking state changes
* Monitoring conditions
* Storing measurements

***

## Required input

The processor requires a data stream containing:
* A timestamp field
* A state field
* At least one sensor value field to cache

***

## Configuration

### Timestamp

Select the field containing the event timestamp. This is used to track when values are buffered.

### State

Select the field containing the state information. This determines when values are cached.

### Sensor Value to Cache

Select the sensor value field that should be cached while the state is active.

## Output

The processor creates a new event containing:
* A timestamp field
* A list of buffered values
* A list of states

### Example

#### Input Event
```json
{
  "deviceId": "sensor01",
  "timestamp": 1586380104915,
  "state": ["active"],
  "temperature": 23.5
}
```

#### Configuration
* Timestamp: timestamp
* State: state
* Sensor Value to Cache: temperature

#### Output Event (when state changes from "active" to "inactive")
```json
{
  "timestamp": 1586380105915,
  "values": [23.5, 24.1, 24.3],
  "state": ["active"]
}
```

## Use Cases

1. **State Monitoring**
   * Cache sensor values
   * Track state changes
   * Monitor conditions
   * Store measurements

2. **Data Analysis**
   * Analyze state patterns
   * Track value changes
   * Monitor conditions
   * Store measurements

## Notes

* Values are cached during active states
* Timestamps are preserved
* State changes trigger updates
* Processing is stateful
* Multiple values can be buffered
