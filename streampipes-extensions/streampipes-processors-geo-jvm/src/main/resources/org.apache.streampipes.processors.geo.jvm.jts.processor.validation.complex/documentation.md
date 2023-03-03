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
Validates geometry of topology
<a href="https://locationtech.github.io/jts/javadoc/org/locationtech/jts/operation/valid/TopologyValidationError.html" target="_blank">erros from JTS</a>.

* **HOLE_OUTSIDE_SHELL**: Indicates that a hole of a polygon lies partially or completely in the exterior of the shell
* **NESTED_HOLES**: Indicates that a hole lies in the interior of another hole in the same polygon
* **DISCONNECTED_INTERIOR**: Indicates that the interior of a polygon is disjoint (often caused by set of contiguous holes splitting the polygon into two parts)
* **SELF_INTERSECTION**: Indicates that two rings of a polygonal geometry intersect
* **RING_SELF_INTERSECTION**: Indicates that a ring self-intersects
* **NESTED_SHELLS**: Indicates that a polygon component of a MultiPolygon lies inside another polygonal component
* **DUPLICATE_RINGS**: Indicates that a polygonal geometry contains two rings which are identical
* **TOO_FEW_POINTS**: Indicates that either a LineString contains a single point or a LinearRing contains 2 or 3 points
* **RING_NOT_CLOSED**: Indicates that a ring is not correctly closed (the first and the last coordinate are different)


***

## Required inputs

* JTS Geometry
* EPSG Code
* Validation Type
* Log Output Option


***

## Configuration

### Point Geometry Field
Input Point Geometry

### EPSG field
Integer value representing EPSG code

### Validation Output
Chose the output result of the filter.
* Valid - all valid events are parsed through
* Invalid - all invalid events are parsed through


### Log Output Option
Options to activate Log-Output to the Pipeline Logger Window with detailed reason why Geometry is invalid


***

### Default Validation Checks

## Output

All events that match the validation output.

### Example
