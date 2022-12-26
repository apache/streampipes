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

## Geo City Name Reverse Decoder

<p align="center">
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

This processor computes city name based on given lat/lng coordinates that are transmitted as fields from an event.
This processor automatically downloads the file cities1000.zip from <a href="http://download.geonames.org/export/dump/cities1000.zip)" target="_blank">Geonames</a>
 ( This file is provided under the <a href="https://creativecommons.org/licenses/by/4.0/)" target="_blank">CC BY 4.0 license</a>).



***

## Required inputs

Input event requires to have latitude and longitude values.

***

## Configuration

### Latitude

The field containing the latitude value.

### Longitude

The field containing the longitude value.

## Output

Outputs a similar event like below.

```
{
  'geoname': 'Colombo'
}
```