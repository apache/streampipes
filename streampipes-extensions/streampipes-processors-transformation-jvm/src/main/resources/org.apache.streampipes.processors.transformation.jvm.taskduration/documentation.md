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

## Task Duration

<p align="center">
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

The Task Duration processor calculates the time duration between state changes in a task field. It supports:
* Task state tracking
* Duration calculation
* Multiple time units
* Process identification
* State transition timing
* Performance measurement

This processor is essential for:
* Measuring task durations
* Tracking process times
* Analyzing state transitions
* Monitoring performance
* Building metrics
* Optimizing workflows

***

## Required input

The processor requires a data stream containing:
* A task field that changes to indicate different states
* A timestamp field for duration calculation

***

## Configuration

### Task Field
Select the field that contains the task state. The processor will calculate the duration between changes in this field.

### Timestamp Field
Select the field containing the timestamp for duration calculation.

### Output Unit
Choose the time unit for the duration output:
* Milliseconds (default)
* Seconds
* Minutes

## Output

The processor creates a new event containing:
* A processId field showing the state transition (format: "previousState-newState")
* A duration field showing the time between state changes in the selected unit

### Example

#### Input Event
```json
{
  "deviceId": "machine01",
  "timestamp": 1586380104915,
  "taskState": "running"
}
```

#### Configuration
* Task Field: taskState
* Timestamp Field: timestamp
* Output Unit: Seconds

#### Output Event (when taskState changes from "running" to "completed")
```json
{
  "processId": "running-completed",
  "duration": 120.5
}
```

## Use Cases

1. **Process Timing**
   * Measure task durations
   * Track state transitions
   * Analyze process times
   * Monitor performance
   * Build metrics

2. **Workflow Analysis**
   * Measure step durations
   * Track transitions
   * Analyze workflows
   * Monitor efficiency
   * Build analytics

3. **Performance Monitoring**
   * Measure state durations
   * Track changes
   * Analyze timing
   * Monitor efficiency
   * Build reports

4. **Quality Control**
   * Measure cycle times
   * Track states
   * Analyze durations
   * Monitor quality
   * Build controls

## Notes

* Task field must change to trigger output
* Duration is calculated between state changes
* Multiple time units supported
* Processing is stateful
* ProcessId shows state transition
* Consider time unit selection
* No delay in processing
* Original values not preserved
* Only outputs on state change
* Duration is always positive