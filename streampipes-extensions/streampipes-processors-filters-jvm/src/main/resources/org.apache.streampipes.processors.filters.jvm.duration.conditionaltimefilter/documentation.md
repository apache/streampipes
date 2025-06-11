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

# Conditional Time Filter

## Description

The **Conditional Time Filter** is a flexible processor that filters a data stream based on a boolean condition being met for a user-defined duration. It can operate in two primary modes:

1.  **On Event Arrival**: It checks if the duration has passed only when a new event arrives. This is useful for event-driven logic.
2.  **On Timer**: It uses a background timer to emit an event at the exact moment the duration is met, even if no new event has arrived. This is ideal for time-sensitive alerts and timeout scenarios.

The processor enriches the output with valuable timing information, including the timestamp of the original event that started the timer and the timestamp when the processor fired.

This processor is useful for scenarios like detecting sustained machine states (e.g., running for 10 minutes), inactivity (e.g., off for 30 minutes), filtering transient alerts, or creating time-aware monitoring logic.

## Required Input

The processor requires a data stream with the following properties:

-   **Boolean Field**: A boolean field that indicates the condition to be monitored (e.g., `isMachineRunning`).
-   **Timestamp Field**: A timestamp field that the processor will use for its duration calculations.

## Output

The processor forwards the event that satisfies the condition, while enriching it with the following new fields:

-   `originalTimestamp`: The timestamp of the event that initiated the timer.
-   `processingTimestamp`: The timestamp when the processor actually sends the event (after the delay).
-   `timeDifference`: The difference in milliseconds between the `processingTimestamp` and `originalEventTimestamp`.

## Configuration Parameters

| Parameter            | Type                   | Description                                                                                                                                                                                                  |
| -------------------- | ---------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **Field to Observe** | Mapping Property       | Select the boolean field to monitor (e.g., `isMachineRunning`).                                                                                                                                              |
| **Timestamp Field** | Mapping Property       | Select the timestamp field to use for all duration calculations.                                                                                                                                             |
| **Duration** | Integer                | The time value for which the condition must be met.                                                                                                                                                      |
| **Duration Unit** | Single Value Selection | The unit for the specified duration (e.g., `Seconds`, `Minutes`, `Hours`).                                                                                                                                   |
| **State to Observe** | Slide Toggle           | The boolean state that should be observed to start the timer. Toggle **on** to trigger when the field is `true`, or **off** to trigger when the field is `false`.                                       |
| **Trigger Mode** | Single Value Selection | Defines when to check if the duration is met. <br>- **On Event Arrival**: Checks only when a new event is received. <br>- **On Timer**: Fires exactly when the time has passed, even without a new event. |
| **Output Mode** | Single Value Selection | Defines how events are emitted after the duration is met. <br>- **Fire Once**: Emits only the first qualifying event. <br>- **Fire Continuously**: Emits all subsequent events while the condition remains true.   |

## Example Usage

### Scenario

You want to monitor a machine that sends status updates. You need to generate an alert if the machine's `isHot` status remains `true` for more than 5 minutes, and you need this alert to be sent at the 5-minute mark precisely, even if the machine doesn't send a new status update at that exact time.

### Configuration

1.  **Field to Observe**: Select the `isHot` boolean field.
2.  **Timestamp Field**: Select the event's primary `timestamp` field.
3.  **Duration**: Set to `5`.
4.  **Duration Unit**: Select `Minutes`.
5.  **State to Observe**: Ensure the toggle is **on** (to observe the `true` state).
6.  **Trigger Mode**: Choose `On Timer` to ensure the event fires at the 5-minute mark.
7.  **Output Mode**: Choose `Fire Once` to generate a single alert.

### Behavior

-   An event arrives with `isHot: true` and `timestamp: 12:00:00`. The processor starts a 5-minute timer.
-   Even if no new events arrive, at `12:05:00`, the processor's internal timer will fire.
-   It will take the event that arrived at 12:00:00, enrich it with the new timestamp fields (`originalTimestamp: 12:00:00`, `processingTimestamp: 12:05:00`), and send it to the next pipeline element.