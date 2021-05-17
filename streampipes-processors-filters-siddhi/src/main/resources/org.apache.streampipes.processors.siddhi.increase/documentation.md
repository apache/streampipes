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

## Trend

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

Detects the increase of a numerical field over a customizable time window. Example: A temperature value increases by 10 percent within 5 minutes.

***

## Required input

There should be a number field in the event to observe the trend.

***

## Configuration

### Value to Observe

Specifies the value field that should be monitored.

### Increase/Decrease

Specifies the type of operation the processor should perform.

### Percentage of Increase/Decrease

Specifies the increase in percent (e.g., 100 indicates an increase by 100 percent within the specified time window).

### Time Window Length (Seconds)

Specifies the size of the time window in seconds.

## Output

Outputs the events if there is a trend observed according to the configuration defined.