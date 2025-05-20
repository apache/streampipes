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

## Boolean Filter

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description
The Boolean Filter processor filters events based on a boolean field value. It supports:
* Exact boolean value matching
* Event forwarding on match
* Simple true/false filtering
* State-based event filtering

This processor is essential for:
* Filtering events by boolean state
* Implementing conditional event routing
* State-based event processing
* Boolean condition filtering

***

## Required Input
The processor requires a data stream containing at least one boolean field to filter on.

***

## Configuration

### Field
Select the boolean field to filter on. The processor will check this field's value against the selected filter value.

### Field Value
Choose whether to keep events where the field value is:
* True - Only events with true values are forwarded
* False - Only events with false values are forwarded

## Output
The processor creates a new event containing all original fields from the input event, but only when the selected boolean field matches the configured value.

### Example

#### Input Event Stream
```json
{
  "deviceId": "sensor01",
  "location": "l1",
  "isActive": true,
  "timestamp": 1586380104915
}
```
```json
{
  "deviceId": "sensor01",
  "location": "l1",
  "isActive": false,
  "timestamp": 1586380105015
}
```

#### Configuration
* Field: isActive
* Field Value: True

#### Output Event
```json
{
  "deviceId": "sensor01",
  "location": "l1",
  "isActive": true,
  "timestamp": 1586380104915
}
```

## Use Cases

1. **State Filtering**
   * Filter active/inactive states
   * Process only enabled devices
   * Handle operational states
   * Filter by status flags

2. **Conditional Processing**
   * Route events by condition
   * Filter by boolean flags
   * Process based on state
   * Handle boolean triggers

## Notes

* Only exact boolean matches are supported
* Events are forwarded unchanged
* No transformation of values
* Simple true/false filtering
* Original event structure preserved