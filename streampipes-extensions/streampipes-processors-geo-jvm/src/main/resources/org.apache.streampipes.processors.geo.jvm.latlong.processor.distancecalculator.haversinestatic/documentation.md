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

## Distance Static Calculator (Haversine)

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

Calculates the distance with the <a href="https://en.wikipedia.org/wiki/Haversine_formula" target="_blank">Haversine formula</a> between a fixed location (e.g., a place) and a latitude/longitude pair of an input
 event.

***

## Required inputs

Requires a data stream that provides latitude and longitude values.

***

## Configuration

Describe the configuration parameters here

### Latitude field

The field containing the latitude value.

### Longitude field

The field containing the longitude value.

### Latitude

The latitude value of the fixed location

### Longitude

The longitude value of the fixed location

## Output

Outputs a similar event like below.

```
{
  'distance': 12.5
}
```
