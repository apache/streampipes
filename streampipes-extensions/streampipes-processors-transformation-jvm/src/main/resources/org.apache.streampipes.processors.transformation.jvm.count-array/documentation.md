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

## Count Array

<p align="center">
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

The Count Array processor counts the number of elements in array/list fields. It supports:
* Array size counting
* List length measurement
* Collection size tracking
* Dynamic array handling

This processor is essential for:
* Measuring array sizes
* Tracking list lengths
* Monitoring collection growth
* Analyzing data volumes

***

## Required input

The processor requires a data stream containing at least one array or list field to count its elements.

***

## Configuration

### List Field

Select the array or list field to count its elements. This field will be used to determine the number of items it contains.

## Output

The processor creates a new event containing:
* All original fields from the input event
* A new field named "countValue" containing the number of elements in the selected array/list

### Example

#### Input Event
```json
{
  "deviceId": "sensor01",
  "measurements": [23.5, 24.1, 25.3, 24.8],
  "timestamp": 1586380104915
}
```

#### Configuration
* List Field: measurements

#### Output Event
```json
{
  "deviceId": "sensor01",
  "measurements": [23.5, 24.1, 25.3, 24.8],
  "timestamp": 1586380104915,
  "countValue": 4
}
```

## Use Cases

1. **Data Analysis**
   * Count array elements
   * Track list sizes
   * Monitor collection growth
   * Measure data volumes

2. **Resource Management**
   * Monitor buffer sizes
   * Track queue lengths
   * Measure storage usage
   * Analyze capacity needs

## Notes

* Only array/list fields can be counted
* Count is calculated per event
* Processing is stateless
* Empty arrays return 0
* Null arrays are not supported