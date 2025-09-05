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

## Numerical Filter

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description
The Numerical Filter processor filters events based on numerical comparisons with a specified threshold. It supports various comparison operations and is ideal for:
* Threshold-based event filtering
* Range-based data selection
* Outlier detection
* Value-based event routing

***

## Required Input
A data stream containing at least one numerical field to filter on.

***

## Configuration

### Field
* Select the numerical field to apply the filter operation on
* The field must contain numeric values

### Operation
Choose from the following comparison operators:
* **<** (Less than)
* **<=** (Less than or equal)
* **>** (Greater than)
* **>=** (Greater than or equal)
* **==** (Equal)
* **!=** (Not equal)

### Threshold Value
* Specify the numerical threshold to compare against
* The value must be a valid number

## Output
The processor forwards the input event only if the numerical comparison evaluates to true.

### Example

#### Input Events
```json
{
  "temperature": 25.5,
  "timestamp": 1586380104915
}
{
  "temperature": 26.0,
  "timestamp": 1586380105015
}
{
  "temperature": 25.8,
  "timestamp": 1586380105115
}
```

#### Example 1: Greater Than Filter
Configuration:
* Field: temperature
* Operation: >
* Threshold Value: 25.8

Output Events:
```json
{
  "temperature": 26.0,
  "timestamp": 1586380105015
}
```

#### Example 2: Range Filter
Configuration:
* Field: temperature
* Operation: >=
* Threshold Value: 25.5

Output Events:
```json
{
  "temperature": 25.5,
  "timestamp": 1586380104915
}
{
  "temperature": 26.0,
  "timestamp": 1586380105015
}
{
  "temperature": 25.8,
  "timestamp": 1586380105115
}
```

#### Example 3: Exact Match Filter
Configuration:
* Field: temperature
* Operation: ==
* Threshold Value: 25.8

Output Events:
```json
{
  "temperature": 25.8,
  "timestamp": 1586380105115
}
```

## Use Cases

1. **Threshold Monitoring**
   * Alert on values exceeding limits
   * Filter out normal readings
   * Monitor critical thresholds
   * Track value ranges

2. **Data Quality**
   * Remove outliers
   * Filter invalid measurements
   * Ensure value ranges
   * Validate sensor data

## Notes

* The processor performs exact numerical comparisons
* For equality checks, a small epsilon (0.000001) is used to handle floating-point precision
* Events that don't match the filter condition are dropped
* The original event structure is preserved in the output
* All numerical types (integer, float, double) are supported