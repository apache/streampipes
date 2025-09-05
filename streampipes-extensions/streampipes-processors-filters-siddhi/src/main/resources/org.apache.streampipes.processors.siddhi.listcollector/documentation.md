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

## List Collector

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description
The List Collector processor aggregates values from a specified field into a list within a configurable batch window. It:
* Collects field values over time
* Creates a list of all values in the window
* Preserves original event data
* Adds a new list field to the output
* Works with any field type

***

## Required Input
The processor requires an input event stream with at least one field to collect values from.

***

## Configuration

### Field
Select the field whose values should be collected into a list. The field can be of any data type.

### Batch Window Size
Specify the number of events to include in each batch window. The processor will collect values from this many events before creating a new list.

## Output
The processor outputs the original event with an additional field containing the collected list of values.

### Example

#### Input Event
```json
{
  "temperature": 25.5,
  "timestamp": 1586380105115
}
```

#### Configuration
* Field: `temperature`
* Batch Window Size: `5`

#### Output Event
```json
{
  "temperature": 25.5,
  "timestamp": 1586380105115,
  "temperature_list": [22.1, 23.4, 24.2, 25.0, 25.5]
}
```

## Use Cases

1. **Data Aggregation**
   * Collect time series data
   * Create value histories
   * Track changes over time
   * Build data windows

2. **Analysis Preparation**
   * Prepare data for statistical analysis
   * Create input for trend detection
   * Generate data for pattern recognition
   * Build feature vectors

3. **Monitoring**
   * Track value distributions
   * Monitor value ranges
   * Analyze value patterns
   * Create value snapshots

## Notes

* The processor creates a new list field with the original field name plus "_list"
* Lists are created after the specified number of events
* Original event data is preserved
* The processor works with any field type
* Lists are cleared after each batch window