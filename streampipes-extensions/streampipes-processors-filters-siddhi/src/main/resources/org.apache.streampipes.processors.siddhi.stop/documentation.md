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

## Stream Stop Detection

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description
The Stream Stop Detection processor monitors an input stream and triggers an event when no new events arrive for a specified duration. It:
* Detects stream interruptions
* Monitors event arrival patterns
* Triggers alerts on stream stops
* Provides timestamp of detection
* Works with any event stream type

***

## Required Input
The processor works with any input event stream and does not require specific fields.

***

## Configuration

### Time Window Length (Seconds)
Specify the duration in seconds to wait for events before triggering the stop detection. If no events arrive within this time window, the processor will output a stop detection event.

## Output
The processor outputs an event with a timestamp and a message indicating that the stream has stopped.

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
* Time Window Length: `60` (seconds)

#### Output Event
```json
{
  "timestamp": 1586380165115,
  "message": "Event stream has stopped"
}
```

## Use Cases

1. **System Monitoring**
   * Detect sensor failures
   * Monitor data source health
   * Track stream reliability
   * Identify connection issues

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
* The output includes a timestamp of when the stop was detected
* The processor works with any event stream type
* The time window is specified in seconds
* The processor provides a clear message indicating stream stop