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

## Value Change

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description
The Value Change processor detects specific transitions in numerical values. It:
* Monitors a selected numerical field
* Detects when the value changes from one specified value to another
* Adds a boolean flag indicating the detected change
* Preserves all original event data
* Works with any numerical field type

***

## Required Input
The processor requires an input event stream containing at least one numerical field to monitor for specific value transitions.

***

## Configuration

### Property to Monitor
Select the numerical field from the input event that should be monitored for value changes.

### From Value
Specify the initial value that should trigger the change detection. The processor will look for transitions from this value.

### To Value
Specify the target value that should complete the change detection. The processor will look for transitions to this value.

## Output
The processor forwards the input event with an additional boolean field named `isChanged` that indicates whether the specified value transition was detected.

### Example

#### Input Event
```json
{
  "temperature": 25.5,
  "timestamp": 1586380105115
}
```

```json
{
  "temperature": 26.0,
  "timestamp": 1586380105116,
}
```

#### Configuration
* Property to Monitor: `temperature`
* From Value: `25.5`
* To Value: `26.0`

#### Output Event
```json
{
  "temperature": 26.0,
  "timestamp": 1586380105116,
  "isChanged": true
}
```

## Use Cases

1. **State Transition Detection**
   * Monitoring system states
   * Detecting mode changes
   * Tracking status transitions
   * Identifying phase changes

2. **Threshold Monitoring**
   * Detecting crossing of specific thresholds
   * Monitoring value ranges
   * Tracking boundary conditions
   * Identifying critical transitions

3. **Process Control**
   * Monitoring control system states
   * Detecting process transitions
   * Tracking operational modes
   * Identifying state changes

## Notes

* The processor only detects exact matches for the specified values
* The change detection is sequential (must go from "From Value" to "To Value")
* The original event structure is preserved
* The boolean flag is added to every event
* The processor maintains state between events
* Values are compared using exact equality