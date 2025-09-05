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

## Boolean to State

<p align="center">
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

The Boolean to State processor converts boolean fields into a descriptive state string. It supports:
* Multiple boolean inputs
* Custom state mapping
* Default state handling
* Runtime name mapping
* JSON configuration
* State prioritization

This processor is essential for:
* Converting boolean states
* Creating descriptive states
* Mapping boolean values
* Handling multiple states
* Building state machines
* Creating human-readable states

***

## Required input

The processor requires a data stream containing at least one boolean field to convert into a state.

***

## Configuration

### Current State

Select one or more boolean fields to monitor. When a field is true, its runtime name will be used as the state value. If multiple fields are true, the first one in the list will be used.

### Default State

Define a default state string to use when all boolean fields are false. This ensures a state is always present in the output.

### Mapping Configuration

Define custom mappings to replace the runtime names with your own state strings. Use JSON format:

```json
{
    "runtimeName1": "Custom State 1",
    "runtimeName2": "Custom State 2"
}
```

## Output

The processor creates a new event containing:
* All original fields from the input event
* A new field named "current_state" containing the state string

### Example

#### Input Event
```json
{
  "deviceId": "machine01",
  "isRunning": true,
  "isError": false,
  "isMaintenance": false,
  "timestamp": 1586380104915
}
```

#### Configuration
* Current State: isRunning, isError, isMaintenance
* Default State: "IDLE"
* Mapping Configuration:
```json
{
    "isRunning": "OPERATIONAL",
    "isError": "ERROR",
    "isMaintenance": "MAINTENANCE"
}
```

#### Output Event
```json
{
  "deviceId": "machine01",
  "isRunning": true,
  "isError": false,
  "isMaintenance": false,
  "timestamp": 1586380104915,
  "current_state": "OPERATIONAL"
}
```

## Use Cases

1. **Equipment Monitoring**
   * Convert device states
   * Map status flags
   * Create state machines
   * Monitor conditions
   * Track operations

2. **Process Control**
   * Map process states
   * Convert conditions
   * Create workflows
   * Monitor phases
   * Track progress

3. **System Integration**
   * Map system states
   * Convert signals
   * Create interfaces
   * Monitor status
   * Track conditions

4. **Quality Control**
   * Map quality states
   * Convert checks
   * Create reports
   * Monitor tests
   * Track results

## Notes

* Only boolean fields can be converted
* State mapping is case-sensitive
* Default state is required
* JSON configuration is optional
* Processing is stateless
* Multiple states require prioritization
* Consider state naming
* Mapping is immediate
* No delay in processing
* State is always present
