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

## Size Measure

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description
The Size Measure processor calculates and appends the size of incoming events. It:
* Measures event size through serialization
* Supports multiple size units (Bytes, Kilobytes, Megabytes)
* Preserves original event data
* Adds size information as a new field

***

## Required Input
The processor works with any input event stream, as it measures the size of the entire event structure.

***

## Configuration

### Size Unit
Select the unit in which the event size should be measured:
* **Bytes**: Raw size in bytes
* **Kilobytes**: Size divided by 1024 (1 KB = 1024 bytes)
* **Megabytes**: Size divided by 1048576 (1 MB = 1024 KB)

## Output
The processor forwards the input event with an additional field named `eventSize` containing the size of the event in the selected unit.

### Example

#### Input Event
```json
{
  "temperature": 25.5,
  "humidity": 60,
  "timestamp": 1586380105115
}
```

#### Configuration
* Size Unit: `Kilobytes`

#### Output Event
```json
{
  "temperature": 25.5,
  "humidity": 60,
  "timestamp": 1586380105115,
  "eventSize": 0.09375
}
```

## Use Cases

1. **Performance Monitoring**
   * Track event sizes over time
   * Monitor data volume
   * Identify large events
   * Optimize data transfer

2. **Resource Planning**
   * Estimate storage requirements
   * Plan network capacity
   * Optimize buffer sizes
   * Scale infrastructure

3. **Debugging**
   * Identify oversized events
   * Track data growth
   * Monitor serialization overhead
   * Troubleshoot performance issues

## Notes

* Size measurement includes all event fields and metadata
* The size is calculated through Java serialization
* Results are stored as double-precision floating-point numbers
* The original event structure is preserved
* The size is measured for each incoming event
* The result field is always named `eventSize`
* Size measurement adds some processing overhead