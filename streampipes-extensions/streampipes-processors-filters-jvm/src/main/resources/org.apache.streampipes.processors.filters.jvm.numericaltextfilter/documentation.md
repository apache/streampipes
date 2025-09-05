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

## Numerical Text Filter

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description
The Numerical Text Filter processor combines numerical and text-based filtering in a single processor. It forwards events only when both the numerical and text conditions are satisfied. This processor is ideal for:
* Complex event filtering
* Multi-criteria event selection
* Data validation
* Event routing based on multiple conditions

***

## Required Input
The processor requires an input event stream containing:
* At least one numerical field for numerical comparison
* At least one text field for string matching

***

## Configuration

### Numerical Filter
* **Field**: Select the numerical field to apply the filter operation on
* **Operation**: Choose from the following comparison operators:
  * **<** (Less than)
  * **<=** (Less than or equal)
  * **>** (Greater than)
  * **>=** (Greater than or equal)
  * **==** (Equal)
  * **!=** (Not equal)
* **Threshold**: Specify the numerical value to compare against

### Text Filter
* **Field**: Select the text field to apply the filter operation on
* **Operation**: Choose from:
  * **MATCHES**: Exact string match
  * **CONTAINS**: Substring match
* **Keyword**: Specify the text to match against

## Output
The processor forwards the input event only if both the numerical and text conditions evaluate to true.

### Example

#### Input Event
```json
{
  "temperature": 25.5,
  "status": "active",
  "timestamp": 1586380104915
}
```

#### Configuration
Numerical Filter:
* Field: temperature
* Operation: >
* Threshold: 20.0

Text Filter:
* Field: status
* Operation: MATCHES
* Keyword: active

#### Output Event
```json
{
  "temperature": 25.5,
  "status": "active",
  "timestamp": 1586380104915
}
```

## Use Cases

1. **Sensor Data Filtering**
   * Filter sensor readings based on value ranges and status
   * Monitor specific conditions in sensor data
   * Validate sensor readings against expected patterns

2. **Event Validation**
   * Ensure events meet both numerical and categorical criteria
   * Validate business rules with multiple conditions
   * Filter events based on complex criteria

3. **Data Quality Control**
   * Filter out invalid or unexpected data combinations
   * Ensure data meets quality thresholds
   * Validate data against business rules

## Notes

* Both numerical and text conditions must be satisfied for an event to be forwarded
* Numerical comparisons use floating-point precision (0.000001 tolerance for equality)
* Text matching is case-sensitive
* The processor preserves the original event structure
* All fields from the input event are included in the output
