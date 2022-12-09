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

## EPSG Code Enricher

<p align="center">
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>


***

## Description

This processor adds ab integer value to the stream. This integer value represents
an [EPSG Code](https://en.wikipedia.org/wiki/EPSG_Geodetic_Parameter_Dataset) as an Spatial Reference System Identifier [(SRID)](https://en.wikipedia.org/wiki/Spatial_reference_system#Identifier).


***

## Required input

None

***

## Configuration

Integer values, representing a spatial reference system [(SRS)](https://en.wikipedia.org/wiki/Spatial_reference_system#Identifier).  Other possible values can be looked up under https://spatialreference.org/ref/epsg/.

### Parameter

4- to 5-digit key integer number. Default value is 4326 representing the World Geodetic System [(WGS84)](https://en.wikipedia.org/wiki/World_Geodetic_System#WGS84).

***
## Output

Adds a number to the event.
