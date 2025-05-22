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

## Boolean Counter

<p align="center">
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

The Boolean Counter processor counts state changes in a boolean field. It supports:
* Rising edge counting (FALSE -> TRUE)
* Falling edge counting (TRUE -> FALSE)
* Both edge counting
* Incremental counting
* State change detection
* Event-based counting

This processor is essential for:
* Counting state transitions
* Tracking signal changes
* Monitoring event frequency
* Measuring cycle counts
* Analyzing state patterns
* Building event counters

***

## Required input

The processor requires a data stream containing at least one boolean field to monitor for state changes.

***

## Configuration

### Boolean Field

Select the boolean field to monitor for state changes. This field will be used to detect edges and increment the counter.

### Flank Parameter

Choose which state changes to count:

* **FALSE -> TRUE**: Count rising edges only
  * Triggers when signal changes from false to true
  * Use for: Counting activations
  * Example: Count device startups

* **TRUE -> FALSE**: Count falling edges only
  * Triggers when signal changes from true to false
  * Use for: Counting deactivations
  * Example: Count device shutdowns

* **BOTH**: Count all state changes
  * Triggers on any state change
  * Use for: Counting all transitions
  * Example: Count all state changes

## Output

The processor creates a new event containing:
* All original fields from the input event
* A new field named "counter" containing the current count

### Example

#### Input Event Stream
```json
[
  {
    "deviceId": "machine01",
    "isRunning": false,
    "timestamp": 1586380104915
  },
  {
    "deviceId": "machine01",
    "isRunning": true,
    "timestamp": 1586380105015
  },
  {
    "deviceId": "machine01",
    "isRunning": false,
    "timestamp": 1586380105115
  }
]
```

#### Configuration 1: Rising Edge Counting
* Boolean Field: isRunning
* Flank Parameter: FALSE -> TRUE

#### Output Event
```json
{
  "deviceId": "machine01",
  "isRunning": true,
  "timestamp": 1586380105015,
  "counter": 1
}
```

#### Configuration 2: Both Edges Counting
* Boolean Field: isRunning
* Flank Parameter: BOTH

#### Output Events
```json
{
  "deviceId": "machine01",
  "isRunning": true,
  "timestamp": 1586380105015,
  "counter": 1
}
```
```json
{
  "deviceId": "machine01",
  "isRunning": false,
  "timestamp": 1586380105115,
  "counter": 2
}
```

## Use Cases

1. **Equipment Monitoring**
   * Count machine cycles
   * Track state changes
   * Monitor activations
   * Count operations
   * Track usage patterns

2. **Process Control**
   * Count process steps
   * Track state transitions
   * Monitor phase changes
   * Count operations
   * Track cycles

3. **Event Detection**
   * Count button presses
   * Track switch changes
   * Monitor transitions
   * Count events
   * Track occurrences

4. **System Analysis**
   * Count state changes
   * Track transitions
   * Monitor patterns
   * Count events
   * Track frequencies

## Notes

* Only boolean fields can be counted
* Counter is incremental
* Count starts at 0
* Processing is stateful
* Counter persists between events
* Events are only emitted on state changes
* Counter is reset on pipeline restart
* Multiple counters require chaining
* Edge detection is immediate