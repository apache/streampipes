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

## String Timer

<p align="center">
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

The String Timer processor measures how long a string field maintains a specific value. It supports:
* String value monitoring
* Duration measurement
* Multiple time units
* Configurable output frequency

This processor is essential for:
* Measuring state durations
* Tracking value persistence
* Monitoring string states
* Calculating time periods

***

## Required input

The processor requires a data stream containing at least one string field to monitor for value changes.

***

## Configuration

### String Field

Select the string field to monitor for value changes. This field will be used to measure how long it maintains a specific value.

### Output Unit

Choose the time unit for the measured duration:
* Milliseconds (default)
* Seconds
* Minutes

### Output Frequency

Define when the processor should emit an output event:
* On Input Event: Emit for every input event
* When String Value Changes: Emit only when the string value changes

## Output

The processor creates a new event containing:
* All original fields from the input event
* A measured_time field showing the duration in the selected unit
* A field_value field showing the previous string value

### Example

#### Input Event
```json
{
  "deviceId": "machine01",
  "status": "running",
  "timestamp": 1586380104915
}
```

#### Configuration
* String Field: status
* Output Unit: Seconds
* Output Frequency: When String Value Changes

#### Output Event (when status changes from "running" to "stopped")
```json
{
  "deviceId": "machine01",
  "status": "stopped",
  "timestamp": 1586380106915,
  "measured_time": 2.0,
  "field_value": "running"
}
```

## Use Cases

1. **State Monitoring**
   * Measure state durations
   * Track value persistence
   * Monitor status changes
   * Calculate time periods

2. **Process Control**
   * Measure process durations
   * Track state changes
   * Monitor operations
   * Calculate times

## Notes

* Only string fields can be monitored
* Time measurement is stateful
* Measurement starts on value change
* Measurement ends on next change
* Output depends on frequency setting

