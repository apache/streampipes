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

## State Buffer Labeler

<p align="center">
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

The State Buffer Labeler processor adds labels to sensor time-series data based on statistical operations and user-defined rules. It supports:
* State-based labeling
* Statistical operations (min, max, average)
* Custom rule definition
* Multiple conditions
* Default labels

This processor is essential for:
* Adding context to data
* Classifying measurements
* Identifying patterns
* Marking conditions

***

## Required input

The processor requires a data stream containing:
* A state field (array of strings)
* A sensor value field (array of numbers)

***

## Configuration

### State Field

Select the field containing the state information. This determines when rules are applied.

### Select a specific state

Add a filter to define which states to evaluate. Use '*' to select all states.

### Sensor values

Select the array containing the sensor values to evaluate against the rules.

### Operation

Define the statistical operation to apply to the sensor values:
* Minimum: Get the lowest value
* Maximum: Get the highest value
* Average: Calculate the mean value

### Condition

Add conditions in the format:
* `<;5;ok` - Label as "ok" if value is less than 5
* `<;10;ok` - Label as "ok" if value is less than 10
* `*;nok` - Default label "nok" for all other cases

## Output

The processor creates a new event containing:
* All original fields from the input event
* A new label field based on the conditions

### Example

#### Input Event
```json
{
  "deviceId": "sensor01",
  "timestamp": 1586380104915,
  "state": ["active"],
  "values": [23.5, 24.1, 24.3]
}
```

#### Configuration
* State Field: state
* Select a specific state: active
* Sensor values: values
* Operation: Average
* Condition: "<;20;cold", "<;30;warm", "*;hot"

#### Output Event
```json
{
  "deviceId": "sensor01",
  "timestamp": 1586380104915,
  "state": ["active"],
  "values": [23.5, 24.1, 24.3],
  "label": "warm"
}
```

## Use Cases

1. **Data Classification**
   * Add context to data
   * Classify measurements
   * Identify patterns
   * Mark conditions

2. **Quality Control**
   * Label quality levels
   * Mark thresholds
   * Identify issues
   * Track conditions

## Notes

* Conditions are evaluated in order
* Default label is required
* State filtering is optional
* Processing is stateless
* Multiple conditions supported
* Statistical operation is applied before condition evaluation
* Input arrays must contain numeric values
* State field must be an array of strings
