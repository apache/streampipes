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

## Field Hasher

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

The Field Hasher uses an algorithm to encode values in a field. The Field Hasher can use MD5, SHA1 or SHA2 to hash field values.

***

## Required input
This processor requires at least one field of type string.

***

## Configuration

### Field
Specifies the string field that will be encoded.

### Hash Algorithm
Specifies the algorithm used to encode the string field. The following algorithms
are available: SHA2, MD5 or SHA1.

## Output
The encoded string field.