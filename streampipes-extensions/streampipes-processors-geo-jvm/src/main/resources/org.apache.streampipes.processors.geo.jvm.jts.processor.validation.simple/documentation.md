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

## Geometry Validation

<p align="center">
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

Checks the geometry event if the geometry is simple and / or empty.

***

## Required inputs

* JTS Geometry
* EPSG Code
* Validation Type
* Validation Output


***

## Configuration

Validates geometry of different validations categories.


### Point Geometry Field
Input Point Geometry

### EPSG field
Integer value representing EPSG code

### Validation Type
* IsEmpty -  Geometry is empty.
* IsSimple - Geometry is simple.  The SFS definition of simplicity follows the general rule that a Geometry is simple if it has no points of self-tangency, self-intersection or other anomalous points.
  * Valid polygon geometries are simple, since their rings must not self-intersect.
  * Linear rings have the same semantics.
  * Linear geometries are simple if they do not self-intersect at points other than boundary points.
  * Zero-dimensional geometries (points) are simple if they have no repeated points.
  * Empty Geometries are always simple!

### Validation Output
Chose the output result of the filter.
* Valid - all valid events are parsed through
* Invalid - all invalid events are parsed through

***

## Output

All events that match the validation output.

### Example
