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

## Value Changed

<p align="center">
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

The Value Changed processor detects changes in field values and outputs events only when changes occur. It supports:
* Any data type comparison
* Dimension-based state tracking
* Change timestamp tracking
* State size management
* Multi-dimensional monitoring

This processor is essential for:
* Detecting value changes
* Tracking state transitions
* Monitoring field updates
* Building change logs
* Implementing change triggers

***

## Required input

The processor requires a data stream containing at least one field to monitor for changes.

***

## Configuration

### Keep Fields

Select the field to monitor for changes. The processor will output an event only when the selected field's value changes.

## Output

The processor creates a new event containing:
* All original fields from the input event
* A new field named "change_detected" containing the timestamp when the change occurred
* Only when the monitored field changes value

### Example

#### Input Event Stream
```json
{
  "deviceId": "sensor01",
  "location": "l1",
  "value": 12,
  "timestamp": 1586380104915
}
```
```json
{
  "deviceId": "sensor01",
  "location": "l1",
  "value": 15,
  "timestamp": 1586380105015
}
```

#### Configuration
* Keep Fields: value

#### Output Event
```json
{
  "deviceId": "sensor01",
  "location": "l1",
  "value": 15,
  "timestamp": 1586380105015,
  "change_detected": 1586380105015
}
```

## Use Cases

1. **Change Detection**
   * Monitor value changes
   * Track state transitions
   * Detect field updates
   * Build change logs

2. **State Monitoring**
   * Track state changes
   * Monitor transitions
   * Detect updates
   * Log changes

## Notes

* Supports any data type
* Processing is stateful
* State size limited to 5000 entries
* Events only emitted on value changes
* Original fields are preserved
* Change timestamps are added
* Dimension-based state tracking
* Multi-dimensional monitoring supported