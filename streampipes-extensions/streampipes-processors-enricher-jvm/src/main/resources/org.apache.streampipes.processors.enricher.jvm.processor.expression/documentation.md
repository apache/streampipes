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

## Math Expression Evaluator

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description
The Math Expression Evaluator processor allows you to perform mathematical calculations on numerical fields using the Apache Commons JEXL library. It:
* Evaluates mathematical expressions
* Supports complex calculations
* Provides access to Java Math functions
* Creates new fields with calculation results

***

## Required Input
The processor requires an input event stream containing at least one numerical field to perform calculations on.

***

## Configuration

See more about JEXL syntax at https://commons.apache.org/proper/commons-jexl/index.html.

### Additional Fields
For each calculation, you need to specify:
* **Field Name**: The name of the new field that will store the calculation result
* **Expression**: The mathematical expression to evaluate using JEXL syntax

### Expression Syntax
The processor supports:
* Basic arithmetic operations (+, -, *, /)
* Mathematical functions from `java.lang.Math`
* References to input field values
* Complex expressions with multiple operations

## Output
The processor forwards the input event with additional fields containing the calculation results.

### Example

#### Input Event
```json
{
  "temperature": 10.1,
  "flowrate": 2
}
```

#### Configuration
* Field Name: `result1`
* Expression: `temperature+12`

* Field Name: `result2`
* Expression: `temperature*flowrate`

#### Output Event
```json
{
  "temperature": 10.1,
  "flowrate": 2,
  "result1": 22.1,
  "result2": 20.2
}
```

## Use Cases

1. **Data Transformation**
   * Convert units
   * Calculate derived metrics
   * Normalize values
   * Scale measurements

2. **Statistical Analysis**
   * Calculate averages
   * Compute standard deviations
   * Perform trend analysis
   * Generate statistical metrics

3. **Business Logic**
   * Calculate costs
   * Compute performance metrics
   * Evaluate business rules
   * Generate derived values

## Notes

* All input fields are available as variables in expressions
* The `Math` class is available for advanced calculations
* Results are stored as double-precision floating-point numbers
* Expressions are evaluated for each incoming event
* Invalid expressions will be logged as errors
* The processor preserves all input fields
