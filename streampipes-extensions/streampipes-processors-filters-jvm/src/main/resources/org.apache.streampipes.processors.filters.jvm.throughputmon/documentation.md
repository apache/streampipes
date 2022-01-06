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

## Throughput monitoring

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description
The Throughput Monitoring processor computes throughput statistics.

***

## Required Input
The processor works with any input event.

***

## Configuration

### Batch Window Size
Specifies the number of events that should be used for calculating throughput statistics.


## Output
The processor outputs a new event containing:
* The current timestamp (timestamp)
* The start time of the batch window (starttime)
* The end time of the batch window (endtime)
* The duration between both windows (duration)
* The number of events collected in the window (should be equal to batch size)
* The throughput in events per second
