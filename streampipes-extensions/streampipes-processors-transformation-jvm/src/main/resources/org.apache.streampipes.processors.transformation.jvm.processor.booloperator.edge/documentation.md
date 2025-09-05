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

## Signal Edge Filter

<p align="center">
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

The Signal Edge Filter processor detects and forwards events when a boolean signal changes state (edge detection). It supports:
* Rising edge detection (FALSE -> TRUE)
* Falling edge detection (TRUE -> FALSE)
* Both edge detection
* Configurable delay
* Event selection options

This processor is essential for:
* Detecting state transitions
* Monitoring signal changes
* Triggering actions on state changes
* Implementing edge-triggered logic

***

## Required input

The processor requires a data stream containing at least one boolean field to monitor for state changes.

***

## Configuration

### Boolean Signal

Select the boolean field to monitor for state changes. This field will be used to detect signal edges.

### Signal Edge

Choose the type of edge to detect:

* **FALSE -> TRUE**: Rising edge detection
  * Triggers when signal changes from false to true
  * Use for: Detecting activation events
  * Example: Device turned on

* **TRUE -> FALSE**: Falling edge detection
  * Triggers when signal changes from true to false
  * Use for: Detecting deactivation events
  * Example: Device turned off

* **BOTH**: Both edge detection
  * Triggers on any state change
  * Use for: Monitoring all transitions
  * Example: Any state change

### Delay

Specify how many events to wait before forwarding the result after an edge is detected:
* Minimum: 0 (immediate forwarding)
* Use for: Debouncing signals
* Example: Wait 5 events to ensure stable state

### Output Event Selection

Choose which event(s) to forward after the delay:

* **First**: Forward only the first event after edge detection
  * Use for: Single trigger actions
  * Example: Start a process once

* **Last**: Forward only the last event after delay
  * Use for: Final state capture
  * Example: Capture stable state

* **All**: Forward all events during delay
  * Use for: Continuous monitoring
  * Example: Track state changes

## Output

The processor creates a new event containing:
* All original fields from the input event
* The event is forwarded based on the configured delay and event selection

### Example

#### Input Event Stream
```json
[
  {
    "deviceId": "sensor01",
    "isActive": false,
    "timestamp": 1586380104915
  },
  {
    "deviceId": "sensor01",
    "isActive": true,
    "timestamp": 1586380105015
  },
  {
    "deviceId": "sensor01",
    "isActive": true,
    "timestamp": 1586380105115
  }
]
```

#### Configuration 1: Rising Edge with First Event
* Boolean Signal: isActive
* Signal Edge: FALSE -> TRUE
* Delay: 0
* Output Event Selection: First

#### Output Event
```json
{
  "deviceId": "sensor01",
  "isActive": true,
  "timestamp": 1586380105015
}
```

#### Configuration 2: Both Edges with Last Event
* Boolean Signal: isActive
* Signal Edge: BOTH
* Delay: 1
* Output Event Selection: Last

#### Output Event
```json
{
  "deviceId": "sensor01",
  "isActive": true,
  "timestamp": 1586380105115
}
```

## Use Cases

1. **Device Monitoring**
   * Detect device power state changes
   * Monitor sensor activation
   * Track equipment status
   * Detect system transitions

2. **Process Control**
   * Trigger actions on state changes
   * Monitor process transitions
   * Detect phase changes
   * Track state machines

## Notes

* Only boolean fields can be monitored
* Edge detection is stateful
* Delay is event-based, not time-based
* Event selection affects output behavior
* Processing is stateful
* Edge detection is immediate
* Delay starts after edge detection
* Event selection applies after delay
