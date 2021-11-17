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

## Cusum (Cumulative Sum)

<!--
<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>
-->

***

## Description

Performs change detection on a single dimension of the incoming data stream. A change is detected if the cumulative deviation from the mean exceeds a certain threshold. This implementation tracks the mean and the standard deviation using Welford's algorithm, which is well suited for data streams.

***

## Required input

The cusum processor requires a data stream that has at least one field containing a numerical value.

***

## Configuration

### Value to observe
Specify the dimension of the data stream (e.g. the temperature) on which to perform change detection. 

### Parameter `k`
`k` controls the sensitivity of the change detector. Its unit are standard deviations. For an observation `x_n`, the Cusum value is `S_n = max(0, S_{n-1} - z-score(x_n) - k)`. Thus, the cusum-score `S` icnreases if `S_{n-1} - z-score(x_n) > k`. 

### Parameter `h`
The alarm theshold in standard deviations. An alarm occurs if `S_n > h` 

## Output

This processor outputs the original data stream plus 

- `cusumLow`: The cusum value for negative changes
- `cusumHigh`: The cusum value for positive changes
- `changeDetectedLow`: Boolean indicating if a negative change was detected
- `changeDetectedHigh`: Boolean indicating if a positive change was detected