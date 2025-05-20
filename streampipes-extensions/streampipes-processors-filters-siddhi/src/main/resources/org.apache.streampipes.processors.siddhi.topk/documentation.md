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

## Top K Analysis

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description
The Top K Analysis processor collects incoming events in a configurable time window and outputs the top or bottom K events based on a specified count value. It:
* Aggregates events within a time window
* Ranks events by their count values
* Supports both ascending and descending order
* Preserves original event data
* Works with any event stream type

***

## Required Input
The processor requires an input event stream with:
* A field containing values to be counted
* A field containing count values for ranking

***

## Configuration

### Field
Select the field whose values should be counted and ranked.

### Count Field
Select the field containing the count values used for ranking the events.

### Batch Window Size
Specify the number of events to include in each batch window.

### Time Window Scale
Choose the time unit for the batch window:
* Hours
* Minutes
* Seconds

### Limit
Specify the maximum number of events to output (K value).

### Order
Select the ranking order:
* Ascending: Outputs the bottom K events
* Descending: Outputs the top K events

## Output
The processor outputs a list of the top or bottom K events from each batch window. Each event in the list contains:
* The value field from the input event
* The count value used for ranking

### Example

#### Input Event
```json
{
  "device_id": "device1",
  "measurement": "temperature",
  "value": 25.5,
  "occurrences": 15,
  "timestamp": 1586380105115
}
```

#### Configuration
* Field: `device_id`
* Count Field: `occurrences`
* Batch Window Size: `60`
* Time Window Scale: `Seconds`
* Limit: `3`
* Order: `Descending`

#### Output Event
```json
{
  "top": [
    {
      "value": "device1",
      "count": 15
    },
    {
      "value": "device2",
      "count": 12
    },
    {
      "value": "device3",
      "count": 10
    }
  ]
}
```

## Use Cases

1. **Performance Analysis**
   * Identify top performing sensors
   * Monitor high-frequency events
   * Track resource usage patterns
   * Analyze system bottlenecks

2. **Anomaly Detection**
   * Find unusual event patterns
   * Identify outliers
   * Monitor system behavior
   * Detect anomalies

3. **Resource Optimization**
   * Identify high-usage resources
   * Monitor system load
   * Track performance metrics
   * Optimize resource allocation

## Notes

* The processor uses a sliding time window for analysis
* Events are ranked based on their count values
* The output includes the original event data
* The time window is configurable in hours, minutes, or seconds
* The processor supports both top K and bottom K analysis