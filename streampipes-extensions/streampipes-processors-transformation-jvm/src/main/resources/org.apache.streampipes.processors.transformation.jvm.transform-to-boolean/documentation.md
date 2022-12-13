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

## Transform to Boolean

<p align="center">
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

This processors transforms numbers and strings to boolean values.


***

## Required input

A string with the values "true", "True", "false", "False" or a number with value 1.0, 1, 0, or 0.0

***

## Configuration

Select fields that should be converted to boolean.

## Output

Selected properties of input events are transformed to booleans.
When the value is not valid an error message is logged and the event is discarde.