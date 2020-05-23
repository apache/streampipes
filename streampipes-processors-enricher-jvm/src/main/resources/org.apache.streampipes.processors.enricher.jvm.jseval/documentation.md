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

## JavaScript Eval

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description
A pipeline element that allows writing user defined JavaScript function to enrich events.

***

## Required input
This processor does not have any specific input requirements.

***

## Configuration
User can specify their custom enrichment logic within the `process` method. Please note that the `process` function 
must be in the following format and it must return a map of data which is compatible with the output schema.
```javascript
    function process(event) {
        // do processing here.
        // return a map with fields that matched defined output schema.
        return {id: event.id, tempInCelsius: (event.tempInKelvin - 273.15)};
    }
```

## Output
A new event with the user defined output schema.