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

## Count Aggregation

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description
The Count Aggregation processor performs counting operations on event streams. It:
* Counts occurrences of values within a configurable time window
* Groups events by a specified field
* Provides real-time counting statistics
* Supports flexible time window configurations
* Preserves timestamp information

***

## Required Input
The processor requires an input event stream containing:
* A timestamp field for window-based processing
* At least one field to count occurrences of

***

## Configuration

### Field to Count
Select the field from the input event that should be used for counting occurrences. This field's values will be grouped and counted.

### Time Window Size
Specify the duration of the time window for aggregation. This determines how many events will be considered in each counting operation.

### Time Window Scale
Choose the time unit for the window size:
* Hours
* Minutes
* Seconds

## Output
The processor outputs events containing:
* `timestamp`: The timestamp of the window
* `value`: The value being counted
* `count`: The number of occurrences within the time window

### Example

#### Input Event
```json
{
  "vehicleId": "V123",
  "timestamp": 1586380105115
}
```

#### Configuration
* Field to Count: `vehicleId`
* Time Window Size: `5`
* Time Window Scale: `Minutes`

#### Output Event
```json
{
  "timestamp": 1586380105115,
  "value": "V123",
  "count": 12
}
```

## Use Cases

1. **Traffic Analysis**
   * Count vehicle passes at intersections
   * Monitor traffic flow rates
   * Track vehicle frequency

2. **Event Monitoring**
   * Count error occurrences
   * Track system events
   * Monitor user actions

3. **Resource Usage**
   * Count API calls
   * Monitor service requests
   * Track resource utilization

## Notes

* The processor uses sliding time windows
* Counts are reset at the end of each window
* Timestamps are preserved from the input events
* The processor groups events by the selected field
* Results are emitted at the end of each time window
