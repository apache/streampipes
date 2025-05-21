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

## Boolean Timer

<p align="center">
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

The Boolean Timer processor measures the duration that a boolean field maintains a specific state. It supports:
* Measuring TRUE state duration
* Measuring FALSE state duration
* Multiple time units (milliseconds, seconds, minutes)
* State change detection
* Duration tracking

This processor is essential for:
* Measuring state durations
* Monitoring active/inactive times
* Tracking operation periods
* Calculating cycle times

***

## Required input

The processor requires a data stream containing at least one boolean field to measure its state duration.

***

## Configuration

### Boolean Field

Select the boolean field to monitor for state duration. This field will be used to measure how long it maintains a specific state.

### Value to Observe

Choose which state to measure the duration for:

* **TRUE**: Measure how long the field stays true
  * Use for: Measuring active periods
  * Example: Machine runtime

* **FALSE**: Measure how long the field stays false
  * Use for: Measuring inactive periods
  * Example: Downtime duration

### Output Unit

Select the time unit for the measured duration:

* **Milliseconds**: Raw millisecond values
  * Use for: Precise timing
  * Example: Response time measurement

* **Seconds**: Duration in seconds (milliseconds / 1000)
  * Use for: General timing
  * Example: Process duration

* **Minutes**: Duration in minutes (milliseconds / 60000)
  * Use for: Long periods
  * Example: Operation time

## Output

The processor creates a new event containing:
* All original fields from the input event
* A new field named "measured_time" containing the duration in the selected unit

### Example

#### Input Event Stream
```json
[
  {
    "deviceId": "machine01",
    "isRunning": false,
    "timestamp": 1586380104915
  },
  {
    "deviceId": "machine01",
    "isRunning": true,
    "timestamp": 1586380105915
  },
  {
    "deviceId": "machine01",
    "isRunning": true,
    "timestamp": 1586380106915
  },
  {
    "deviceId": "machine01",
    "isRunning": false,
    "timestamp": 1586380107915
  }
]
```

#### Configuration 1: TRUE State in Seconds
* Boolean Field: isRunning
* Value to Observe: TRUE
* Output Unit: Seconds

#### Output Event
```json
{
  "deviceId": "machine01",
  "isRunning": false,
  "timestamp": 1586380107915,
  "measured_time": 2.0
}
```

## Use Cases

1. **Equipment Monitoring**
   * Measure machine runtime
   * Track downtime periods
   * Monitor active states
   * Calculate cycle times

2. **Process Control**
   * Measure process duration
   * Track state changes
   * Monitor operation times
   * Calculate response times

## Notes

* Only boolean fields can be measured
* Processing is stateful
* Time measurement is based on system time
* Measurement starts when selected state is detected
* Measurement ends when opposite state is detected
* Output is only generated when measurement ends
* Time units are automatically converted
* Original event fields are preserved
