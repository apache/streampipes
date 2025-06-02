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

## Threshold Detector

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description
The Threshold Detector processor monitors numerical values and detects when they cross specified thresholds. It:
* Compares numerical values against a threshold
* Appends a boolean flag indicating threshold status
* Supports various comparison operations
* Preserves all input data while adding threshold information

***

## Required Input
The processor requires an input event stream containing at least one numerical field to monitor.

***

## Configuration

### Field
Select the numerical field to monitor for threshold crossing.

### Operation
Choose from six comparison operations:
* **<** (Less than)
* **<=** (Less than or equal)
* **>** (Greater than)
* **>=** (Greater than or equal)
* **==** (Equal)
* **!=** (Not equal)

### Threshold Value
Specify the numerical threshold value to compare against.

## Output
The processor forwards the input event with an additional boolean field `thresholdDetected` indicating whether the threshold condition was met.

### Example

#### Input Event
```json
{
  "temperature": 25.5,
  "humidity": 60,
  "timestamp": 1586380104915
}
```

#### Configuration
* Field: `temperature`
* Operation: `>`
* Threshold Value: `25.0`

#### Output Event
```json
{
  "temperature": 25.5,
  "humidity": 60,
  "timestamp": 1586380104915,
  "thresholdDetected": true
}
```

## Use Cases

1. **Monitoring & Alerting**
   * Monitor sensor readings
   * Detect threshold crossings
   * Trigger alerts
   * Track value ranges

2. **Quality Control**
   * Monitor process parameters
   * Detect out-of-range values
   * Ensure quality standards
   * Track compliance

3. **Data Analysis**
   * Analyze value distributions
   * Track threshold events
   * Monitor trends
   * Identify patterns

## Notes

* The processor preserves all input fields
* The `thresholdDetected` field is always appended
* Floating-point comparisons use a small epsilon (0.000001) for equality
* All events are forwarded, regardless of threshold status
* The threshold check is performed on the exact numerical value
