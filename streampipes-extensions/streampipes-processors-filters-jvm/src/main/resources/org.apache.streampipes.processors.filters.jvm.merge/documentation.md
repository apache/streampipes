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

## Merge By Time

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description
The Merge By Time processor combines events from two data streams based on their timestamps. It merges events when their timestamps fall within a specified time interval of each other. This processor is essential for:
* Synchronizing data from multiple sources
* Correlating events across different streams
* Creating unified views of time-aligned data
* Implementing time-based event matching

<p align="center"> 
    <img width="300px;" src="merge_description.png" class="pe-image-documentation"/>
</p>
***

## Required Input
Each input stream must contain a timestamp field that can be used for matching events.

***

## Configuration

### Timestamp Selection
* **Stream 1 Timestamp**: Select the timestamp field from the first input stream
* **Stream 2 Timestamp**: Select the timestamp field from the second input stream

### Time Interval
* Specifies the maximum time difference (in milliseconds) between events for them to be considered a match
* Events are merged when: |timestamp_stream_1 - timestamp_stream_2| < interval
* Example: With interval = 1000ms, events within 1 second of each other will be merged

## Output
The processor creates a new event containing all fields from both input events when their timestamps match within the specified interval.

### Example

#### Input Events
Stream 1:
```json
{
  "deviceId": "sensor01",
  "temperature": 25.5,
  "timestamp": 1586380104915
}
```

Stream 2:
```json
{
  "location": "room1",
  "humidity": 45,
  "timestamp": 1586380105015
}
```

#### Configuration
* Stream 1 Timestamp: timestamp
* Stream 2 Timestamp: timestamp
* Time Interval: 1000ms

#### Output Event
```json
{
  "deviceId": "sensor01",
  "temperature": 25.5,
  "location": "room1",
  "humidity": 45,
  "timestamp": 1586380105015
}
```

## Use Cases

1. **Sensor Data Correlation**
   * Combine temperature and humidity readings
   * Merge location and environmental data
   * Synchronize multiple sensor streams
   * Create unified sensor views

2. **Event Synchronization**
   * Align events from different sources
   * Match related events across streams
   * Create time-aligned data views
   * Implement temporal joins

## Notes

* Events are matched based on absolute time difference
* Buffer management prevents memory overflow
* Events outside the time interval are not merged
* Original event structure is preserved in output
* Timestamps must be in milliseconds
* Both streams must have valid timestamp fields