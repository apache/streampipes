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

## Enrich By Merging 

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description
Merges two data streams by enriching one of the streams with the properties of the other stream. The output frequency is the same as the frequency of the stream which is enriched.
***

## Required input
None
***

## Configuration

* Select the stream which should be enriched with the properties of the other stream.
  * The last event of the stream is hold in state and each event of the other stream is enriched by the properties the user selected

## Output
The compose processor has a configurable output that can be selected by the user at pipeline modeling time.