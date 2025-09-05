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

## Frequency Change Monitor

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description
The Frequency Change Monitor processor detects significant changes in event arrival frequency. It:
* Monitors event arrival patterns
* Detects percentage-based frequency changes
* Supports configurable time windows
* Works with any input event stream
* Preserves original event data

***

## Required Input
The processor works with any input event stream. No specific input requirements are needed.

***

## Configuration

### Time Window Length
Specify the duration of the time window in seconds. The processor will monitor event frequency changes within this window.

### Time Unit
Choose the time unit for the window size:
* Hours (hrs)
* Minutes (min)
* Seconds (sec)

### Percentage of Increase/Decrease
Specify the threshold for frequency change detection:
* Value represents percentage change (e.g., 100 means 100% increase)
* Range: 0-500%
* Step size: 1%

## Output
The processor outputs the original event when a significant frequency change is detected within the specified time window.

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
* Percentage of Increase/Decrease: `100`

#### Output Event
The processor will output the event if the frequency of events changes by 100% or more within a 30-second window.

## Use Cases

1. **Anomaly Detection**
   * Identify unusual event patterns
   * Detect sudden changes in data flow
   * Monitor system behavior changes
   * Track event rate anomalies

2. **Performance Monitoring**
   * Monitor system throughput changes
   * Track data processing rates
   * Identify bottlenecks
   * Measure system responsiveness

3. **Quality Assurance**
   * Ensure consistent data flow
   * Monitor data collection reliability
   * Track system performance
   * Validate data source health

## Notes

* The processor detects percentage-based changes in event frequency
* The time window is configurable in hours, minutes, or seconds
* Original event data is preserved in the output
* The processor works with any type of input event
* Results are emitted when the frequency change threshold is exceeded
