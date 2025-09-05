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

## Compose

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description
The Compose processor merges two event streams by combining their properties. It supports:
* Real-time event stream merging
* Last-event state tracking
* Custom output field selection
* Dynamic event composition
* Stateful event processing

This processor is essential for:
* Combining data from multiple sources
* Creating unified event views
* Merging related event streams
* Building composite event structures

***

## Required Input
The processor requires two input streams:
* First stream: Any event stream with at least one property
* Second stream: Any event stream with at least one property

***

## Configuration

### Output Selection
During pipeline modeling, you can select which fields from each stream should be included in the output event. The processor will:
* Keep the last event from each stream in memory
* Merge selected fields when new events arrive
* Forward the combined event with selected fields

## Output
The processor creates a new event containing the selected fields from both input streams. The output is generated whenever:
* A new event arrives from either stream
* The last event from the other stream is available

### Example

#### Input Event Stream 1
```json
{
  "deviceId": "sensor01",
  "temperature": 25.5,
  "timestamp": 1586380104915
}
```

#### Input Event Stream 2
```json
{
  "location": "room1",
  "humidity": 45.2,
  "timestamp": 1586380104915
}
```

#### Configuration
* Output Fields: deviceId, temperature, location, humidity

#### Output Event
```json
{
  "deviceId": "sensor01",
  "temperature": 25.5,
  "location": "room1",
  "humidity": 45.2
}
```

## Use Cases

1. **Data Integration**
   * Combine sensor readings
   * Merge related metrics
   * Create unified views
   * Join event streams

2. **Event Enrichment**
   * Add context to events
   * Combine related data
   * Create composite events
   * Build rich event structures

## Notes

* Events are merged in real-time
* Last event state is maintained
* Output fields are configurable
* Original event structure is preserved
* Events are forwarded on any new input
* State is cleared on pipeline stop