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

## String Counter

<p align="center">
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

The String Counter processor counts changes in string field values. It supports:
* Value change detection
* Change pair tracking
* Incremental counting
* State transition monitoring

This processor is essential for:
* Counting value changes
* Tracking state transitions
* Monitoring string patterns
* Measuring change frequency

***

## Required input

The processor requires a data stream containing at least one string field to monitor for value changes.

***

## Configuration

### String Field

Select the string field to monitor for value changes. This field will be used to detect changes and increment the counter.

## Output

The processor creates a new event containing:
* All original fields from the input event
* A new field named "counter" containing the current count of value changes
* A new field named "change_from" containing the previous value
* A new field named "change_to" containing the new value

### Example

#### Input Event Stream
```json
{
  "deviceId": "sensor01",
  "status": "idle"
}
```
```json
{
  "deviceId": "sensor01",
  "status": "running"
}
```
```json
{
  "deviceId": "sensor01",
  "status": "idle"
}
```

#### Configuration
* String Field: status

#### Output Event
```json
{
  "deviceId": "sensor01",
  "status": "running",
  "change_from": "idle",
  "change_to": "running",
  "counter": 1
}
```
```json
{
  "deviceId": "sensor01",
  "status": "idle",
  "change_from": "running",
  "change_to": "idle",
  "counter": 2
}
```

## Use Cases

1. **State Monitoring**
   * Track state changes
   * Count transitions
   * Monitor patterns
   * Measure frequency

2. **Process Analysis**
   * Analyze workflows
   * Track sequences
   * Count cycles
   * Monitor operations

## Notes

* Only counts actual value changes
* Ignores consecutive identical values
* Processing is stateful
* Counter is incremental
* Events are only emitted on value changes
* Original event fields are preserved
* Change pairs are tracked (from -> to)
* Counter starts at 1 for first change

