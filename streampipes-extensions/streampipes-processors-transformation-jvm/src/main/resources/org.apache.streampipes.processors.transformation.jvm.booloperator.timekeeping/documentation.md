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

## Measure Time Between Two Sensors

<p align="center">
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

The Measure Time Between Two Sensors processor calculates the time difference between two boolean signals. It supports:
* Signal timing measurement
* Event sequence tracking
* Time difference calculation
* Signal counting
* Multiple time units (milliseconds, seconds, minutes)

This processor is essential for:
* Measuring process times
* Tracking signal sequences
* Analyzing delays
* Monitoring performance

***

## Required input

The processor requires a data stream containing:
* A left boolean field (start signal)
* A right boolean field (end signal)

***

## Configuration

### Left Field

Select the boolean field that starts the timer. This signal marks the beginning of the time measurement.

### Right Field

Select the boolean field that ends the timer. This signal triggers the output event with the measured time.

### Output Unit

Choose the time unit for the output:
* **Milliseconds**: Raw time difference
* **Seconds**: Time difference divided by 1000
* **Minutes**: Time difference divided by 60000

## Output

The processor creates a new event containing:
* All original fields from the input event
* A "measured_time" field showing the time between signals in the selected unit
* A "counter" field showing the number of completed measurements

### Example

#### Input Event Stream
```json
[
  {
    "deviceId": "machine01",
    "timestamp": 1586380104915,
    "startSignal": false,
    "endSignal": false
  },
  {
    "deviceId": "machine01",
    "timestamp": 1586380105015,
    "startSignal": true,
    "endSignal": false
  },
  {
    "deviceId": "machine01",
    "timestamp": 1586380105115,
    "startSignal": true,
    "endSignal": true
  }
]
```

#### Configuration
* Left Field: startSignal
* Right Field: endSignal
* Output Unit: Seconds

#### Output Event
```json
{
  "deviceId": "machine01",
  "timestamp": 1586380105115,
  "startSignal": true,
  "endSignal": true,
  "measured_time": 1.0,
  "counter": 1
}
```

## Use Cases

1. **Process Timing**
   * Measure process cycle times
   * Track signal sequences
   * Analyze delays
   * Monitor performance

2. **Performance Analysis**
   * Measure response times
   * Track operation sequences
   * Analyze system delays
   * Monitor equipment performance

## Notes

* Only boolean fields can be monitored
* Processing is stateful
* Time measurement is based on system time
* Counter resets at Long.MAX_VALUE
* Multiple start signals are buffered
* Output is triggered by end signal
* Original event fields are preserved