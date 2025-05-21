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

## Merge By Schema

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description
The Merge By Schema processor combines events from two input streams that share the same event schema. It ensures data consistency by only merging events that have identical structures. This processor is essential for:
* Schema validation
* Data consistency enforcement
* Stream synchronization
* Event structure verification

***

## Required Input
The processor requires two input streams that must have:
* Identical event schemas
* Matching property names and types
* Compatible data structures

***

## Configuration
No additional configuration is required. The processor automatically:
* Validates schema compatibility
* Ensures structural consistency
* Maintains data integrity

## Output
The processor forwards events from both input streams, ensuring they maintain their original structure and values.

### Example

#### Input Stream 1 Event
```json
{
  "temperature": 25.5,
  "humidity": 60,
  "timestamp": 1586380104915
}
```

#### Input Stream 2 Event
```json
{
  "temperature": 26.0,
  "humidity": 65,
  "timestamp": 1586380105015
}
```

#### Output Events
Both events are forwarded as they share the same schema:
```json
{
  "temperature": 25.5,
  "humidity": 60,
  "timestamp": 1586380104915
}
```
```json
{
  "temperature": 26.0,
  "humidity": 65,
  "timestamp": 1586380105015
}
```

## Use Cases

1. **Data Validation**
   * Ensure consistent data structures
   * Validate event schemas
   * Maintain data quality
   * Enforce schema compliance

2. **Stream Synchronization**
   * Combine compatible data streams
   * Merge homogeneous data sources
   * Maintain data consistency
   * Ensure structural alignment

3. **Quality Assurance**
   * Verify data structure integrity
   * Validate event formats
   * Ensure schema compatibility
   * Maintain data standards

## Notes

* The processor throws an exception if schemas do not match
* All events maintain their original structure
* No data transformation is performed
* Events are forwarded as-is from both streams
* Schema validation is performed at runtime