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

## Transform to Boolean

<p align="center">
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

The Transform to Boolean processor converts string or number fields into boolean values. It supports:
* String to boolean conversion
* Number to boolean conversion
* Multiple field transformation
* In-place value modification

This processor is essential for:
* Converting data types
* Creating boolean flags
* Transforming values
* Standardizing data

***

## Required input

The processor requires a data stream containing at least one string or number field to transform into a boolean.

***

## Configuration

### Transform Fields

Select one or more string or number fields to transform into boolean values. The transformation rules are:
* Strings: "true" or "1" becomes true, "false" or "0" becomes false
* Numbers: 1 or 1.0 becomes true, 0 or 0.0 becomes false

## Output

The processor creates a new event containing:
* All original fields from the input event
* The selected fields with their values transformed to boolean

### Example

#### Input Event
```json
{
  "deviceId": "sensor01",
  "status": "true",
  "value": 1,
  "timestamp": 1586380104915
}
```

#### Configuration
* Transform Fields: status, value

#### Output Event
```json
{
  "deviceId": "sensor01",
  "status": true,
  "value": true,
  "timestamp": 1586380104915
}
```

## Use Cases

1. **Data Standardization**
   * Convert string states
   * Transform numeric flags
   * Standardize values
   * Create boolean flags

2. **Condition Creation**
   * Create boolean conditions
   * Transform thresholds
   * Convert states
   * Build flags

## Notes

* Only string and number fields can be transformed
* String comparison is case-insensitive
* Numbers use 1/0 logic
* Processing is stateless
* Multiple fields can be transformed