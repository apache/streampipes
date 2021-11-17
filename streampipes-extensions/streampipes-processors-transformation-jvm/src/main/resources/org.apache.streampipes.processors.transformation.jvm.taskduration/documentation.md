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

## Task Duration


***

## Description

This processors computes the duration of a task, i.e., a field containing a task description. It outputs an event
 every time this task value changes and computes the duration between the first occurrence of this task and the
  current event. For instance, you can use this event to calculate the time a specific process step requires.
***

## Required input

A timestamp value is required and a field containing a task value.

***

## Configuration

(no further configuration required)

## Output

Emits an event that contains the process step, built from the names of the first task identifier and the identifier
 of the subsequent task. In addition, the duration is part of the output event, provided in milliseconds.