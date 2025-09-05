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

## Boolean Logical Operator

<p align="center">
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

The Boolean Logical Operator processor performs logical operations on multiple boolean fields. It supports:
* Standard logical operations (AND, OR, NOT, XOR, XNOR, NOR)
* Multiple input fields
* Single output field
* Truth table based operations
* Complex boolean logic
* State combination

This processor is essential for:
* Combining multiple conditions
* Implementing decision logic
* Creating composite states
* Building safety interlocks
* Monitoring complex conditions
* Implementing control logic

***

## Required input

The processor requires a data stream containing at least one boolean field for NOT operations, or at least two boolean fields for all other operations.

***

## Configuration

### Properties

Select one or more boolean fields to use as operands for the logical operation. The number of fields required depends on the selected operator:
* NOT: Exactly one field
* All other operators: Two or more fields

### Boolean Operator

Choose the logical operation to perform:

* **AND**: True only if all inputs are true
  * Use for: Requiring all conditions
  * Example: All safety switches must be on

* **OR**: True if any input is true
  * Use for: Accepting any condition
  * Example: Any warning condition is active

* **NOT**: Inverts the input value
  * Use for: Negating conditions
  * Example: Invert a status flag

* **XOR**: True if odd number of inputs are true
  * Use for: Detecting mismatches
  * Example: Check if states are different

* **XNOR**: True if even number of inputs are true
  * Use for: Detecting matches
  * Example: Verify state agreement

* **NOR**: True only if all inputs are false
  * Use for: Detecting inactive states
  * Example: Check if all systems are off

## Output

The processor creates a new event containing:
* All original fields from the input event
* A new boolean field named "boolean-operations-result" with the result of the logical operation

### Example

#### Input Event
```json
{
  "deviceId": "machine01",
  "safetySwitch1": true,
  "safetySwitch2": true,
  "emergencyStop": false,
  "timestamp": 1586380104915
}
```

#### Configuration 1: AND Operation
* Properties: safetySwitch1, safetySwitch2
* Operator: AND

#### Output Event
```json
{
  "deviceId": "machine01",
  "safetySwitch1": true,
  "safetySwitch2": true,
  "emergencyStop": false,
  "timestamp": 1586380104915,
  "boolean-operations-result": true
}
```

#### Configuration 2: NOT Operation
* Properties: emergencyStop
* Operator: NOT

#### Output Event
```json
{
  "deviceId": "machine01",
  "safetySwitch1": true,
  "safetySwitch2": true,
  "emergencyStop": false,
  "timestamp": 1586380104915,
  "boolean-operations-result": true
}
```

## Use Cases

1. **Safety Systems**
   * Combine multiple safety interlocks
   * Monitor emergency conditions
   * Implement fail-safe logic
   * Validate safety states
   * Create composite safety checks

2. **Process Control**
   * Combine process conditions
   * Implement control logic
   * Monitor state combinations
   * Create decision points
   * Build process interlocks

3. **Quality Monitoring**
   * Combine quality checks
   * Implement acceptance criteria
   * Monitor multiple conditions
   * Create composite quality states
   * Build validation rules

4. **System Monitoring**
   * Combine status indicators
   * Monitor system states
   * Implement health checks
   * Create alert conditions
   * Build diagnostic rules

## Notes

* All input fields must be boolean
* NOT operator requires exactly one input field
* All other operators require at least two input fields
* Operations are evaluated left-to-right
* Result is always boolean
* Null inputs are treated as false
* The output field is always named "boolean-operations-result"
* Processing is stateless
* Multiple operations require chaining processors
* Consider operation precedence in complex logic
