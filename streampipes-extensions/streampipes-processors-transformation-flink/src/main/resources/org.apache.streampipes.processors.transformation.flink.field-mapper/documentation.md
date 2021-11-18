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

## Field Mapper

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

Replaces one or more fields with a new field and computes a hash value of these fields

***

## Configuration

* Fields: Fields that will be mapped into a property
* Name of the new field

***

## Example
Merge two fields into a hash value
### Input  event
```
{
  "timestamp":1586380104915,
  "mass_flow":4.3167,
  "temperature":40.05,
  "sensorId":"flowrate01"
}
```

### Configuration
* Fields: mass_flow, temperature
* Name of new field: demo

### Output event 
```
{
  "timestamp":1586380104915,
  "sensorId":"flowrate01"
  "demo":"8ae11f5c83610104408d485b73120832",
}
```