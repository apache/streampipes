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

## Sliding Statistics Summary

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

Calculate simple descriptive summary statistics for a selected property over a defined time interval.

***

## Required input
Requires a numerical property

## Configuration

* Define a value to observe
* Select the time field
* Group the event streams by an identifier
* Set the time window size and scale (Seconds, Minutes, Hours)

## Output
The statistics are appended to the event and contain:
* Minimum
* Maximum
* Sum
* Standard Deviation
* Variance
