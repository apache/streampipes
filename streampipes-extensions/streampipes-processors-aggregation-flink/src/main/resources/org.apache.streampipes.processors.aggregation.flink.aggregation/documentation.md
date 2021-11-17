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

## Aggregation

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

Performs different aggregation functions based on a sliding time window (e.g., average, sum, min, max)

***

## Required input

The aggregation processor requires a data stream that has at least one field containing a numerical value.

***

## Configuration

### Group by
The aggregation function can be calculated separately (partitioned) by the selected field value. 

### Output every
The frequency in which aggregated values are sent in seconds.

### Time window
The size of the time window in seconds

### Aggregated Value
The field used for calculating the aggregation value.

## Output

This processor appends the latest aggregated value to every input event that arrives.