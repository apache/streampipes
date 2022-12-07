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

## State Buffer

<p align="center">
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

Buffers values of a sensor, while state does not change.
Select a state field in the event. Events are buffered as long as state field does not change.
When it changes result
event is emitted.

***

## Required input

Define the state and sensor value field

### Timestamp

A mapping property for a timestamp field

### State

Select the field representing the state

### Sensor value to cache

Select the field with the numerical values to buffer

## Output

Emits a new event on state change, with the fields `timestamp`, `state`, and a list containing all `sensor values`.
