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

## Frequency Monitor

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description
The Frequency Monitor processor detects when events stop arriving within a specified time window. It:
* Monitors event arrival frequency
* Detects when events stop arriving
* Supports configurable time windows
* Works with any input event stream
* Preserves original event data

***

## Required Input
The processor works with any input event stream. No specific input requirements are needed.

***

## Configuration

### Time Window Length
Specify the duration of the time window in seconds. If no events arrive within this window, the processor will detect a frequency change.

### Time Unit
Choose the time unit for the window size:
* Hours (hrs)
* Minutes (min)
* Seconds (sec)

## Output
The processor outputs the original event when no events arrive within the specified time window.

### Example

#### Input Event
```json
{
  "temperature": 25.5,
  "timestamp": 1586380105115
}
```

#### Configuration
* Time Window Length: `30`
* Time Unit: `sec`

#### Output Event
The processor will output the last received event if no new events arrive within 30 seconds.

## Use Cases

1. **System Monitoring**
   * Detect sensor failures
   * Monitor data source health
   * Identify connection issues
   * Track system availability

2. **Alert Generation**
   * Trigger alerts on data gaps
   * Notify on system downtime
   * Report on service interruptions
   * Monitor data flow continuity

3. **Quality Assurance**
   * Ensure data stream continuity
   * Monitor data collection reliability
   * Track system performance
   * Validate data source health

## Notes

* The processor detects the absence of events
* The time window is configurable in hours, minutes, or seconds
* Original event data is preserved in the output
* The processor works with any type of input event
* Results are emitted when the time window expires without new events
