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

## Split Array

<p align="center">
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

This processor takes an array of event properties and creates an event for each of them.
Further property of the events can be added to each element.

***

## Required input

This processor works with any event that has a field of type ``list``.

***

## Configuration

### Keep Fields

Fields of the event that should be kept in each resulting event.

### List field

The name of the field that contains the list values that should be split.


## Output

This data processor produces an event with all fields selected by the ``Keep Fields`` parameter and all fields of the
 selected list field.