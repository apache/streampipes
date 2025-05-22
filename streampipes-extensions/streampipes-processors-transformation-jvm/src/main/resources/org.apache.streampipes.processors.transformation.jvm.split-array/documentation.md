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

## Split Array

<p align="center">
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

The Split Array processor transforms array fields into multiple individual events, with each array element becoming a separate event. It supports:
* Array element extraction
* Context preservation
* Nested field handling
* Custom field naming

This processor is essential for:
* Converting batch data into individual events
* Processing array elements independently
* Distributing array data across streams
* Enabling element-wise analysis

***

## Required input

The processor requires a data stream containing at least one array field. The array can contain elements of any supported data type:
* Numbers (integer or float)
* Strings
* Booleans
* Objects
* Nested arrays

***

## Configuration

### Array Field Selection

Select the array field to split into individual events. The field must be an array type.

### Keep Fields

Select one or more fields from the input event that should be preserved in each output event. These fields will be copied to each output event.

## Output

For each element in the input array, the processor creates a new event containing:
* The array element as a single value in a field named "array_value"
* All selected fields from the original event

### Example

#### Input Event
```json
{
  "deviceId": "sensor123",
  "timestamp": 1586380104915,
  "measurements": [22.5, 23.1, 22.8, 23.4],
  "status": "active"
}
```

#### Configuration
* Array Field: measurements
* Keep Fields: deviceId, timestamp, status

#### Output Events
```json
// First element
{
  "deviceId": "sensor123",
  "timestamp": 1586380104915,
  "status": "active",
  "array_value": 22.5
}

// Second element
{
  "deviceId": "sensor123",
  "timestamp": 1586380104915,
  "status": "active",
  "array_value": 23.1
}

// Third element
{
  "deviceId": "sensor123",
  "timestamp": 1586380104915,
  "status": "active",
  "array_value": 22.8
}

// Fourth element
{
  "deviceId": "sensor123",
  "timestamp": 1586380104915,
  "status": "active",
  "array_value": 23.4
}
```

## Use Cases

1. **Batch Processing**
   * Split batch sensor readings
   * Process multi-measurement data
   * Handle grouped observations
   * Transform batch uploads

2. **Data Distribution**
   * Distribute workload across processors
   * Enable parallel processing
   * Balance processing load
   * Scale data processing

## Notes

* Output events maintain original event order
* Empty arrays produce no output events
* Null array elements are preserved
* Processing is stateless
* Memory usage scales with array size
* Nested fields are handled automatically