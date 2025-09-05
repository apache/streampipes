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

## Trigonometry Functions

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description
The Trigonometry Functions processor performs trigonometric calculations on numerical values. It:
* Supports basic trigonometric functions (sin, cos, tan)
* Works with any numerical field type
* Preserves original event data
* Adds calculation results as new fields

***

## Required Input
The processor requires an input event stream containing at least one numerical field to perform trigonometric calculations on.

***

## Configuration

### Alpha
Select the field from the input event that should be used as the angle (in radians) for the trigonometric calculation.

### Operation
Choose one of the following trigonometric functions:
* **sin**: Calculates the sine of the angle
* **cos**: Calculates the cosine of the angle
* **tan**: Calculates the tangent of the angle

## Output
The processor forwards the input event with an additional field named `trigonometryResult` containing the result of the trigonometric calculation.

### Example

#### Input Event
```json
{
  "angle": 1.57,
  "timestamp": 1586380105115
}
```

#### Configuration
* Alpha: `angle`
* Operation: `sin`

#### Output Event
```json
{
  "angle": 1.57,
  "timestamp": 1586380105115,
  "trigonometryResult": 0.9999996829318346
}
```

## Use Cases

1. **Signal Processing**
   * Waveform analysis
   * Signal filtering
   * Phase calculations
   * Frequency analysis

2. **Geometric Calculations**
   * Angle conversions
   * Distance calculations
   * Position tracking
   * Navigation systems

3. **Scientific Computing**
   * Physics simulations
   * Engineering calculations
   * Mathematical modeling
   * Data analysis

## Notes

* Input angles must be in radians
* Results are stored as double-precision floating-point numbers
* The original event structure is preserved
* The calculation is performed for each incoming event
* The result field is always named `trigonometryResult`