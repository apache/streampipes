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

## Buffer Geometry

<p align="center">
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

Creates a buffer Polygon Geometry from a geometry
***

## Required inputs

* WKT of a JTS Geometry
* Integer value representing EPSG code
* Distance
* Cap Style
* Join Style
* Mitre-Limit
* Side
* Simplify Factor
* Quadrant Segments
***

## Configuration

### 1st parameter
Input Geometry

### 2nd parameter
EPSG code value

### 3rd parameter
Distance
the buffer distance around in geometry in meter

### 4rd parameter
Cap Style
CAP_ROUND - the usual round end caps
CAP_FLAT - end caps are truncated flat at the line ends
CAP_SQUARE - end caps are squared off at the buffer distance beyond the line ends 

### 5rd parameter
Simplify Factor
The default simplify factor Provides an accuracy of about 1%, which matches the accuracy of the 
default Quadrant Segments parameter.

### 6rd parameter
Quadrant Segments
The default number of facets into which to divide a fillet of 90 degrees. A value of 8 gives 
less than 2% max error in the buffer distance. For a max error of < 1%, use QS = 12. 
For a max error of < 0.1%, use QS = 18.

### 7rd parameter
Join Style
Defines the corners in a buffer
JOIN_ROUND - the usual round join
JOIN_MITRE - corners are "sharp" (up to a distance limit)
JOIN_BEVEL - corners are beveled (clipped off). 

### 8rd parameter
Mitre-Limit
mitre ratio limit (only affects mitered join style)

### 9rd parameter
Side
: 'left' or 'right' performs a single-sided buffer on the geometry, with the buffered side 
relative to the direction of the line. This is only applicable to LINESTRING geometry and 
does not affect POINT or POLYGON geometries.

***

## Output



### Example

