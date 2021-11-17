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

## Speed Calculator

***

## Description

Calculates the speed (in km/h) based on latitude/longitude values in a data stream. Therefore it uses the GPS and timestamps values of consecutive events. 
It calculates the distance between two points (events) and how much time has passed. Based on those values the speed is calculated.

***

## Required input

Requires a data stream that provides latitude and longitude values as well as a timestamp.

***

## Configuration

### Timestamp field

### Latitude field

### Longitude field

### Count window
Describes the number of stored events, used for the calculation. 
E.g. a value of 5 means that thhe current event and the event (t-5) are used for the speed calculation.

## Output
Appends the calculated speed in km/h.