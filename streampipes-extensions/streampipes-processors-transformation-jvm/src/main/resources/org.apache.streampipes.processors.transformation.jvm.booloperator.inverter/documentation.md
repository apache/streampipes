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

## Boolean Inverter

<p align="center">
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

The Boolean Inverter processor inverts the value of a boolean field in a data stream. It supports:
* Single field inversion
* TRUE to FALSE conversion
* FALSE to TRUE conversion
* In-place value modification
* Simple boolean logic
* Direct value negation

This processor is essential for:
* Negating boolean conditions
* Inverting control signals
* Complementing state values
* Reversing logic gates
* Creating opposite states
* Implementing NOT operations

***

## Required input

The processor requires a data stream containing at least one boolean field to invert.

***

## Configuration

### Invert Field

Select the boolean field to invert. This field's value will be negated (TRUE becomes FALSE, FALSE becomes TRUE).

## Output

The processor creates a new event containing:
* All original fields from the input event
* The selected field with its value inverted

### Example

#### Input Event
```json
{
  "deviceId": "sensor01",
  "isActive": true,
  "timestamp": 1586380104915
}
```

#### Configuration
* Invert Field: isActive

#### Output Event
```json
{
  "deviceId": "sensor01",
  "isActive": false,
  "timestamp": 1586380104915
}
```

## Use Cases

1. **Control Systems**
   * Invert control signals
   * Negate status flags
   * Reverse logic gates
   * Complement states
   * Create opposite conditions

2. **State Management**
   * Invert state values
   * Negate status indicators
   * Reverse boolean flags
   * Complement conditions
   * Create inverse states

3. **Logic Operations**
   * Implement NOT gates
   * Negate conditions
   * Reverse boolean logic
   * Complement expressions
   * Create opposite states

4. **Signal Processing**
   * Invert digital signals
   * Negate binary values
   * Reverse boolean states
   * Complement conditions
   * Create inverse signals

## Notes

* Only boolean fields can be inverted
* Inversion is in-place
* Original value is replaced
* Processing is stateless
* Multiple inversions require chaining
* Consider logic implications
* Inversion is immediate
* No delay in processing
* No additional fields created
* Simple boolean negation