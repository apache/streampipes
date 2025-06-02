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

## Trend Analysis

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description
The Trend Analysis processor monitors numerical values and detects significant increases or decreases within a configurable time window. It:
* Detects percentage-based value changes
* Supports both increase and decrease detection
* Uses configurable time windows
* Preserves original event data
* Works with any numerical field

***

## Required Input
The processor requires an input event stream with at least one numerical field to monitor for trends.

***

## Configuration

### Value to Observe
Select the numerical field that should be monitored for trends.

### Operation Type
Choose the type of trend to detect:
* Increase: Detects when values increase by the specified percentage
* Decrease: Detects when values decrease by the specified percentage

### Percentage Change
Specify the percentage threshold for trend detection:
* For increase: Values must increase by this percentage
* For decrease: Values must decrease by this percentage
* Range: 0-500%
* Step size: 1%

### Time Window Length (Seconds)
Specify the duration in seconds to monitor for the trend.

## Output
The processor outputs the original event when a significant trend is detected within the specified time window.

### Example

#### Input Event
```json
{
  "device_id": "device1",
  "measurement": "temperature",
  "value": 25.5,
  "timestamp": 1586380105115
}
```

#### Configuration
* Value to Observe: `value`
* Operation Type: `Increase`
* Percentage Change: `10`
* Time Window Length: `300`

#### Output Event
```json
{
  "device_id": "device1",
  "measurement": "temperature",
  "value": 28.0,
  "timestamp": 1586380205115
}
```

## Use Cases

1. **Anomaly Detection**
   * Detect sudden temperature changes
   * Monitor pressure variations
   * Track resource usage spikes
   * Identify unusual patterns

2. **Performance Monitoring**
   * Track system metrics
   * Monitor resource utilization
   * Analyze performance trends
   * Detect degradation

3. **Quality Assurance**
   * Monitor process parameters
   * Track product quality metrics
   * Detect process deviations
   * Ensure consistent output

## Notes

* The processor detects percentage-based changes
* The time window is specified in seconds
* The output includes the original event data
* The processor works with any numerical field
* Results are emitted when the trend is detected