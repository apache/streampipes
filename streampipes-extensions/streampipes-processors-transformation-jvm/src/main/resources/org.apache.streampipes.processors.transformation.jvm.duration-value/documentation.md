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

## Calculate Duration

<p align="center">
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

The Calculate Duration processor computes the time difference between two timestamps. It supports:
* Time difference calculation
* Multiple time units
* Start/end timestamp selection
* Duration measurement

This processor is essential for:
* Measuring time periods
* Calculating durations
* Analyzing intervals
* Tracking time spans

***

## Required input

The processor requires a data stream containing at least two timestamp fields to calculate the duration between them.

***

## Configuration

### Start Timestamp

Select the field containing the start timestamp. This timestamp marks the beginning of the duration period.

### End Timestamp

Select the field containing the end timestamp. This timestamp marks the end of the duration period.

### Time Unit

Select the unit for the calculated duration:
* Milliseconds (default)
* Seconds
* Minutes

## Output

The processor creates a new event containing:
* All original fields from the input event
* A new field named "duration" containing the calculated time difference in the selected unit

### Example

#### Input Event
```json
{
  "deviceId": "machine01",
  "startTime": 1586380104915,
  "endTime": 1586380105915,
  "operation": "process1"
}
```

#### Configuration
* Start Timestamp: startTime
* End Timestamp: endTime
* Time Unit: Seconds

#### Output Event
```json
{
  "deviceId": "machine01",
  "startTime": 1586380104915,
  "endTime": 1586380105915,
  "operation": "process1",
  "duration": 1.0
}
```

## Use Cases

1. **Process Monitoring**
   * Measure process duration
   * Track operation times
   * Monitor cycle times
   * Calculate periods

2. **Performance Analysis**
   * Measure response times
   * Track execution times
   * Monitor durations
   * Calculate intervals

## Notes

* Both timestamps must be present
* Timestamps must be valid
* End time must be after start time
* Processing is stateless
* Multiple durations require chaining
* Negative durations are not supported