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

## Projection

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description
The Projection processor allows you to select a subset of fields from input events, creating a new event with only the specified fields. This processor is essential for:
* Reducing data volume
* Focusing on relevant fields
* Data privacy
* Stream optimization

***

## Required Input
The processor works with any input event stream containing one or more fields.

***

## Configuration
At pipeline development time, you can select which fields to include in the output event.

## Output
The processor creates a new event containing only the selected fields from the input event.

### Example

#### Input Event
```json
{
  "temperature": 25.5,
  "humidity": 60,
  "pressure": 1013,
  "timestamp": 1586380104915,
  "device_id": "sensor_001",
  "location": "room_101"
}
```

#### Configuration
Selected fields:
* temperature
* humidity
* timestamp

#### Output Event
```json
{
  "temperature": 25.5,
  "humidity": 60,
  "timestamp": 1586380104915
}
```

## Use Cases

1. **Data Volume Reduction**
   * Remove unnecessary fields
   * Reduce network bandwidth
   * Optimize storage
   * Improve processing speed

2. **Data Privacy**
   * Remove sensitive fields
   * Anonymize data
   * Control data exposure
   * Comply with regulations

3. **Stream Optimization**
   * Focus on relevant data
   * Reduce downstream processing
   * Improve pipeline efficiency
   * Optimize resource usage

## Notes

* The processor preserves the original values of selected fields
* Fields not selected are completely removed from the output
* The order of fields in the output may differ from the input
* All field types are supported
* The processor can handle any number of fields