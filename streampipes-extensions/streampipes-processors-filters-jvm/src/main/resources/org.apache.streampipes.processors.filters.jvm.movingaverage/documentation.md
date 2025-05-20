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

## Moving Average

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description
The Moving Average processor smooths numerical data streams by calculating either the mean or median of the last n values. This processor is essential for:
* Reducing noise in sensor data
* Smoothing out fluctuations
* Identifying trends
* Improving data quality

***

## Required Input
A numerical field is required in the input stream.

***

## Configuration

### Numerical Field
* Select the numerical field to be smoothed
* The field must contain numeric values

### N Value
* Specifies the window size (number of previous values to consider)
* Larger values create smoother output but increase latency
* Smaller values are more responsive but may show more noise

### Method
Choose between two smoothing methods:
* **Mean**: Calculates the arithmetic average of the last n values
* **Median**: Uses the middle value of the last n values (better for handling outliers)

## Output
The processor appends a new field named "filterResult" containing the smoothed value.

### Example

#### Input Events
```json
{
  "temperature": 25.5,
  "timestamp": 1586380104915
}
{
  "temperature": 26.0,
  "timestamp": 1586380105015
}
{
  "temperature": 25.8,
  "timestamp": 1586380105115
}
```

#### Configuration
* Numerical Field: temperature
* N Value: 3
* Method: Mean

#### Output Events
```json
{
  "temperature": 25.5,
  "timestamp": 1586380104915,
  "filterResult": 25.5
}
{
  "temperature": 26.0,
  "timestamp": 1586380105015,
  "filterResult": 25.75
}
{
  "temperature": 25.8,
  "timestamp": 1586380105115,
  "filterResult": 25.77
}
```

## Use Cases

1. **Sensor Data Processing**
   * Smooth temperature readings
   * Filter noise from measurements
   * Stabilize sensor outputs
   * Improve data quality

2. **Trend Analysis**
   * Identify long-term patterns
   * Reduce short-term fluctuations
   * Highlight significant changes
   * Monitor system behavior

## Notes

* The processor maintains a sliding window of the last n values
* Mean method is more sensitive to outliers
* Median method is more robust against outliers
* Window size affects smoothing intensity
* Original values are preserved in the output
* First n-1 events will have partial windows