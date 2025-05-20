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

## Sequence Monitor

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description
The Sequence Monitor processor detects when events stop arriving from a stream for a specified duration. It:
* Monitors event arrival patterns
* Detects gaps in event sequences
* Triggers when no events arrive for a specified time
* Preserves original event data
* Works with any event stream type

***

## Required Input
The processor requires an input event stream. It works with any event stream type and does not require specific fields.

***

## Configuration

### Duration
Specify the time duration (in seconds) to wait for events before triggering. If no events arrive within this duration, the processor will output the last received event.

## Output
The processor outputs the last received event when no new events arrive for the specified duration.

### Example

#### Input Event
```json
{
  "sensor_id": "sensor1",
  "temperature": 25.5,
  "timestamp": 1586380105115
}
```

#### Configuration
* Duration: `60` (seconds)

#### Output Event
The processor will output the last received event if no new events arrive within 60 seconds.

## Use Cases

1. **Stream Monitoring**
   * Detect stream interruptions
   * Monitor data flow continuity
   * Identify connection issues
   * Track stream health

2. **Alert Generation**
   * Trigger alerts on stream stops
   * Notify on data gaps
   * Monitor system health
   * Detect anomalies

3. **Quality Assurance**
   * Ensure continuous data flow
   * Monitor data consistency
   * Track stream reliability
   * Validate system performance

## Notes

* The processor triggers when no events arrive for the specified duration
* Original event data is preserved in the output
* The processor works with any event stream type
* The duration is specified in seconds
* The processor outputs the last received event
