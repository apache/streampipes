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

## Google Maps Geocoding

<p align="center">
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

This processor computes the latitude and longitude values from a location (a place name such as "Karlsruhe, Germany
") and adds the result to the event.

***

## Required inputs

Input event requires to have a field which contains the name of a place.

***

## Configuration

### Place

The field of the input event that should be used to compute the lat/lng values.

## Output

Outputs a similar event like below.

```
{
  'latitude': 6.927079,
  'longitude': 79.861244  
}
```