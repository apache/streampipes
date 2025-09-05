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

## String To State

<p align="center">
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

The String To State processor transforms string properties into a list of state values. It supports:
* Multiple string inputs
* List-based state output
* Field value preservation
* State collection

This processor is essential for:
* Converting strings to states
* Collecting multiple states
* Preserving field values
* Creating state lists

***

## Required input

The processor requires a data stream containing at least one string field to convert into a state.

***

## Configuration

### State Field

Select one or more string fields to convert into states. The values of these fields will be collected into a list of states.

## Output

The processor creates a new event containing:
* All original fields from the input event
* A new field named "current_state" containing a list of the selected string field values

### Example

#### Input Event
```json
{
  "deviceId": "sensor01",
  "status": "running",
  "mode": "normal"
}
```

#### Configuration
* State Fields: status, mode

#### Output Event
```json
{
  "deviceId": "sensor01",
  "status": "running",
  "mode": "normal",
  "current_state": ["running", "normal"]
}
```

## Use Cases

1. **State Collection**
   * Gather multiple states
   * Track field values
   * Monitor statuses
   * Collect modes

2. **State Analysis**
   * Analyze state combinations
   * Track value patterns
   * Monitor field changes
   * Process state lists

## Notes

* Multiple fields can be selected
* Output is always a list
* Original fields are preserved
* Processing is stateless
* Empty selections result in empty list
* Field values are preserved as-is
