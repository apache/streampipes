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

## Buffer Point Geometry

<p align="center">
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

Creates a buffer polygon geometry from a point geometry
***

## Required inputs

* JTS Geometry
* EPSG Code
* Distance
* Cap Style
* Simplify Factor
* Quadrant Segments
***

## Configuration

### Geometry Field
Input Point Geometry

### EPSG field
Integer value representing EPSG code

### Distance
The buffer distance around the geometry in meter

### Cap Style
Defines the endcap style of the buffer.
CAP_ROUND - the usual round end caps
CAP_SQUARE - end caps are squared off at the buffer distance beyond the line ends


### Simplify Factor
The default simplify factor provides an accuracy of about 1%, which matches the accuracy of the
default Quadrant Segments parameter.

### Quadrant Segments
The default number of facets into which to divide a fillet of 90 degrees.

***

## Output
A polygon geometry with EPSG code. Shape is defined by input parameters.

<p align="center">
    <img src="output.png" width="400px;" class="pe-image-documentation"/>
</p>

### Example

