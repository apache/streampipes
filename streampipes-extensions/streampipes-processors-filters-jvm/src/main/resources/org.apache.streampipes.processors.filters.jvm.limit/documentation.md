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

## Rate Limit

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description
The Rate Limit processor controls the frequency of events in a data stream by applying various windowing strategies. It supports:
* Time-based rate limiting
* Event count-based rate limiting
* Cron-based rate limiting
* Group-based rate limiting
* Multiple event selection strategies

This processor is essential for:
* Controlling data flow rates
* Reducing system load
* Implementing sampling strategies
* Managing resource utilization

***

## Required Input
The processor works with any input event stream.

***

## Configuration

### Grouping Settings
* **Enable Grouping**: When enabled, rate limiting is applied separately to each group
* **Grouping Field**: Field to use as the grouping key (only used when grouping is enabled)

### Window Configuration
Select one of the following window types:

1. **Time Window**
   * Window size in milliseconds
   * Events are grouped by time intervals
   * Example: 1000ms window emits events every second

2. **Length Window**
   * Window size in event count
   * Events are grouped by count
   * Example: Window size of 10 emits every 10 events

3. **Cron Window**
   * Cron expression for scheduling
   * Events are grouped by schedule
   * Example: `0 * * ? * *` emits every minute

### Event Selection
Choose how events are selected from each window:
* **First**: Emit the first event in the window
* **Last**: Emit the last event in the window
* **All**: Emit all events in the window

## Output
The processor outputs events based on the configured window and selection strategy. The output maintains the original event structure.

### Example

#### Input Events
```json
{
  "deviceId": "sensor01",
  "temperature": 25.5,
  "timestamp": 1586380104915
}
{
  "deviceId": "sensor01",
  "temperature": 26.0,
  "timestamp": 1586380105015
}
{
  "deviceId": "sensor01",
  "temperature": 25.8,
  "timestamp": 1586380105115
}
```

#### Configuration
* Window Type: Time Window
* Window Size: 1000ms
* Event Selection: Last
* Grouping: Disabled

#### Output Events
```json
{
  "deviceId": "sensor01",
  "temperature": 25.8,
  "timestamp": 1586380105115
}
```

## Use Cases

1. **Data Sampling**
   * Reduce high-frequency sensor data
   * Sample large event streams
   * Control data flow rates
   * Implement periodic reporting

2. **Resource Management**
   * Prevent system overload
   * Manage downstream processing
   * Control storage rates
   * Optimize network usage

## Notes

* Windows are processed independently for each group
* Event selection is applied after window completion
* State is maintained per group
* Windows are cleared after processing
* Cron expressions follow Quartz scheduler format
* Time windows use system time
* Length windows count all events
