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

## Math Operation

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description
The Math Operation processor performs arithmetic calculations between two numerical fields in an event. It:
* Supports basic arithmetic operations (+, -, *, /, %)
* Works with any numerical field type
* Preserves original event data
* Adds calculation results as new fields

***

## Required Input
The processor requires an input event stream containing at least two numerical fields to perform calculations on.

***

## Configuration

### Left Operand
Select the field from the input event that should be used as the left operand in the calculation.

### Right Operand
Select the field from the input event that should be used as the right operand in the calculation.

### Operation
Choose one of the following arithmetic operations:
* Addition (+)
* Subtraction (-)
* Multiplication (*)
* Division (/)
* Modulo (%)

## Output
The processor forwards the input event with an additional field named `calculationResult` containing the result of the arithmetic operation.

### Example

#### Input Event
```json
{
  "temperature": 25.5,
  "humidity": 60,
  "timestamp": 1586380105115
}
```

#### Configuration
* Left Operand: `temperature`
* Right Operand: `humidity`
* Operation: `*`

#### Output Event
```json
{
  "temperature": 25.5,
  "humidity": 60,
  "timestamp": 1586380105115,
  "calculationResult": 1530.0
}
```

## Use Cases

1. **Data Transformation**
   * Calculate derived metrics
   * Convert units
   * Scale measurements
   * Normalize values

2. **Business Logic**
   * Compute costs
   * Calculate performance metrics
   * Evaluate business rules
   * Generate derived values

3. **Sensor Data Processing**
   * Combine sensor readings
   * Calculate averages
   * Normalize measurements
   * Scale sensor values

## Notes

* Both operands must be numerical values
* Division by zero will result in an error
* Results are stored as double-precision floating-point numbers
* The original event structure is preserved
* The calculation is performed for each incoming event
* The result field is always named `calculationResult`