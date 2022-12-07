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

## State Buffer Labeler

<p align="center">
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

Apply a rule to a time-series recorded during a state of a machine.
(E.g. when minimum value is lower then 10, add label `not ok` else add label `ok`)


***

## Required input

Requires a list with sensor values and a field defining the state

### Sensor values

An array representing sensor values recorded during the state.

### State field

A field representing the state when the sensor values where recorded.

***

## Configuration

### Select a specific state
When you are interested in the values of a specific state add it here.
All other states will be ignored. To get results of all states enter `*`

### Operation
Operation that will be performed on the sensor values (calculate `maximim`, or `average`, or `minimum`)

### Condition
Define a rule which label to add. Example: `<;5;nok` means when the calculated value is smaller then 5 add label ok.
The default label can be defined with `*;nok`.
The first rule that is true defines the label. Rules are applied in the same order as defined here.


## Output
Appends a new field  with the label defined in the Condition Configuration
