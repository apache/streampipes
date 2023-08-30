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

## Number Rounder

<p align="center">
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

This processor rounds numeric values to the given digit/precision.

***

## Required input

This processor requires an event that provides numbers.

***

## Configuration

### Fields to Be Rounded

Select which fields of the event should be rounded.

### Number of Digits

Specify the number of digits after the decimal point to round/keep, e.g., if number is 2.8935 and #digits is 3,
the result will be 2.894.

## Output

The output of this processor is the same event with the fields selected by the ``Fiels to Be Rounded`` parameter rounded
to ``Number of Digits`` digits.