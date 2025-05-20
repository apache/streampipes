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

## Static Math Operation

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description
The Static Math Operation processor performs arithmetic calculations between a numerical field and a static value. It:
* Supports basic arithmetic operations (+, -, *, /, %)
* Works with any numerical field type
* Uses a configurable static value as one operand
* Updates the input field with the calculation result
* Preserves other event fields

***

## Required Input
The processor requires an input event stream containing at least one numerical field to perform calculations on.

***

## Configuration

### Left Operand
Select the field from the input event that should be used as the left operand in the calculation.

### Right Operand Value
Specify the static numerical value that should be used as the right operand in the calculation.

### Operation
Choose one of the following arithmetic operations:
* Addition (+)
* Subtraction (-)
* Multiplication (*)
* Division (/)
* Modulo (%)

## Output
The processor updates the selected input field with the result of the arithmetic operation.

### Example

#### Input Event
```json
{
  "temperature": 25.5,
  "timestamp": 1586380105115
}
```

#### Configuration
* Left Operand: `temperature`
* Right Operand Value: `2.0`
* Operation: `*`

#### Output Event
```json
{
  "temperature": 51.0,
  "timestamp": 1586380105115
}
```

## Use Cases

1. **Unit Conversion**
   * Converting between measurement units
   * Scaling values
   * Normalizing data
   * Applying conversion factors

2. **Data Transformation**
   * Applying constant offsets
   * Scaling measurements
   * Adjusting values
   * Normalizing ranges

3. **Signal Processing**
   * Amplifying signals
   * Attenuating values
   * Applying gains
   * Signal conditioning

## Notes

* The processor updates the input field directly
* All calculations are performed using double-precision floating-point arithmetic
* Division by zero will result in an error
* The modulo operation works with floating-point numbers
* The original field name is preserved
* Other event fields remain unchanged