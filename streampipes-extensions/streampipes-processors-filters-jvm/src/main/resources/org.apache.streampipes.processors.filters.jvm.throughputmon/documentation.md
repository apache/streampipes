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

## Throughput Monitor

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description
The Throughput Monitor processor calculates and reports event processing throughput statistics. It:
* Measures event processing rates
* Tracks batch processing windows
* Calculates events per second
* Provides detailed timing information

***

## Required Input
The processor works with any input event stream, as it focuses on measuring event flow rather than event content.

***

## Configuration

### Batch Window Size
Specifies the number of events that should be collected before calculating throughput statistics. This determines:
* How frequently throughput is calculated
* The granularity of the measurements
* The trade-off between accuracy and reporting frequency

## Output
The processor outputs a new event containing:
* `timestamp`: Current system timestamp
* `starttime`: Start time of the batch window
* `endtime`: End time of the batch window
* `duration`: Duration of the batch window in milliseconds
* `eventcount`: Number of events processed in the window
* `throughput`: Events per second (calculated as eventcount / (duration/1000))

### Example

#### Input Events (3 events with batch size 3)
```json
{
  "sensorValue": 42,
  "timestamp": 1586380104915
}
{
  "sensorValue": 43,
  "timestamp": 1586380105015
}
{
  "sensorValue": 44,
  "timestamp": 1586380105115
}
```

#### Configuration
* Batch Window Size: `3`

#### Output Event
```json
{
  "timestamp": 1586380105115,
  "starttime": 1586380104915,
  "endtime": 1586380105115,
  "duration": 200,
  "eventcount": 3,
  "throughput": 15.0
}
```

## Use Cases

1. **Performance Monitoring**
   * Monitor pipeline throughput
   * Track processing rates
   * Identify bottlenecks
   * Measure system performance

2. **Load Testing**
   * Measure processing capacity
   * Test system limits
   * Validate performance
   * Benchmark improvements

3. **Resource Planning**
   * Estimate resource needs
   * Plan capacity
   * Optimize configurations
   * Scale infrastructure

## Notes

* The processor uses a sliding window approach
* Throughput is calculated as events per second
* All timing is in milliseconds
* The batch window size affects measurement granularity
* Events are counted as they arrive
* The processor resets after each batch window
