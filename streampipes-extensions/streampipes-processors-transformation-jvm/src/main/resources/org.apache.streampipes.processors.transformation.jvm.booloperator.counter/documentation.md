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

## Boolean Counter

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

This processor monitors a boolean value and counts how often the value of the boolean changes. 
A user can configure whether the changes from FALSE to TRUE, TRUE to FALSE, or BOTH changes should be counted.

***

## Required input

A boolean value is required in the data stream and can be selected with the field mapping.

### Boolean Field

The boolean value to be monitored.

***

## Configuration

A user can configure whether the changes from TRUE to FALSE, FALSE to TRUE, or all changes of the boolean value should be counted.

### Flank parameter

Either:
* TRUE -> FALSE: Increase counter on a true followed by a false 
* FALSE -> TRUE: Increase counter on a false followed by a true
* BOTH: Increase counter on each change of the boolean value on two consecutive events

## Output

Adds an additional numerical field with the current count value to the event. Events are just emitted when the counter changes.
Runtime Name: countField