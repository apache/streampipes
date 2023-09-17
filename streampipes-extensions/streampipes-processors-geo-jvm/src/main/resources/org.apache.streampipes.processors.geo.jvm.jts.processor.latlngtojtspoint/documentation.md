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

## Latitude Longitude To JTS Point

<p align="center">
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

This processor creates a JTS Point geometry from  latitude and longitude value.

***

## Required inputs

*  Ontology Vocabulary Latitude
*  Ontology Vocabulary Longitude
*  Integer value representing EPSG Code


***

## Configuration

Creates a JTS Geometry Point from Longitude (x) and Latitude (y) values in the coordinate reference system represented by the EPSG code.
An empty point geometry is created if latitude or longitude value is missing in the event (e.g. null value) or values are out of range. Allowed values for Longitude are between -180.00 and 180.00; Latitude values between -90.00 and 90.00.

### 1st parameter
Latitude value

### 2nd parameter
Longitude value

### 3rd parameter
EPSG code value

***

## Output

Adds a point geometry in the Well Known Text notation and in Longitude (x)  Latitude (y) axis order to the stream.

### Example
* Input stream: <br/>
  `{latitude=48.5622, longitude=-76.3501, EPSG=4326}`

* Output Stream <br/>
  `{latitude=48.5622, longitude=-76.3501, EPSG=4326, geom_wkt=POINT (-76.3501 48.5622)}`
