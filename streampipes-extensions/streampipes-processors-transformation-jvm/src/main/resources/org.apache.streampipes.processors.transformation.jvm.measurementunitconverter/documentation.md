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

## Measurement Unit Converter

<p align="center">
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

The Measurement Unit Converter processor automatically converts values between different units of measurement. It supports any unit type that is compatible with the input field's measurement unit, including:

* Length (meters, feet, inches, etc.)
* Mass (kilograms, pounds, ounces, etc.)
* Temperature (Celsius, Fahrenheit, Kelvin)
* Volume (liters, gallons, cubic meters, etc.)
* Pressure (Pascal, bar, PSI, etc.)
* Speed (m/s, km/h, mph, etc.)
* Area (square meters, acres, etc.)
* Time (seconds, minutes, hours, etc.)
* Energy (joules, kilowatt-hours, etc.)
* Power (watts, horsepower, etc.)

This processor is essential for:
* Standardizing measurements across systems
* Converting between international and US units
* Ensuring consistent data representation
* Supporting multi-regional deployments
* Enabling system interoperability

***

## Required input

The processor requires:
1. A numerical field that has a measurement unit defined
2. The field must be of type measurement property

***

## Configuration

### Field

Select the numerical field containing the value to convert. The field must have a measurement unit defined.

### Output Unit

Select the desired unit of measurement for the output value. The available units are dynamically populated based on the input field's measurement unit type.

## Output

The processor creates a new event containing:
* All original fields from the input event
* The selected field is updated with the converted value

### Example

#### Input Event
```json
{
  "sensorId": "temp01",
  "temperature": 20.0,
  "humidity": 65,
  "timestamp": 1586380104915
}
```

#### Configuration
* Field: temperature (with unit Celsius)
* Output Unit: Fahrenheit

#### Output Event
```json
{
  "sensorId": "temp01",
  "temperature": 68.0,
  "humidity": 65,
  "timestamp": 1586380104915
}
```

## Use Cases

1. **International Operations**
   * Convert between metric and imperial units
   * Standardize measurements across regions
   * Support global data analysis
   * Enable multi-market compliance

2. **System Integration**
   * Convert between different system standards
   * Normalize data from multiple sources
   * Enable cross-platform compatibility
   * Support legacy system integration

3. **Scientific Applications**
   * Convert between scientific units
   * Support experimental data analysis
   * Enable cross-discipline collaboration
   * Maintain measurement precision

4. **Industrial Processes**
   * Convert process measurements
   * Standardize control parameters
   * Support equipment interoperability
   * Enable cross-vendor integration

## Notes

* The input field must have a measurement unit defined
* Only compatible units are available for selection
* The conversion is performed in-place
* The original value is replaced with the converted value
* All conversions maintain numerical precision
* Conversion formulas follow international standards
* Invalid unit combinations are rejected
* Null values are preserved in output
* Conversion is stateless
* Conversion accuracy depends on input precision
* Temperature conversions handle both relative and absolute scales