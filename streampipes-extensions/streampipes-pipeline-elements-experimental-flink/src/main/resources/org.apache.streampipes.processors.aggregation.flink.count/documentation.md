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

## Count

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

Performs an aggregation based on a given field and outputs the number of occurrences.
Example: Count the number of vehicle positions per vehicleId.
The Count aggregation requires a time window, used to perform the count aggregation and a field used to aggregate
values.

***

## Required input
There is no specific input required.

***

## Configuration
### FieldToCount    
Specifies the field containing the values that should be counted.

### TimeWindowSize  
Specifies the size of the time window and consequently the number of values that are aggregated each time. 

### Time Window Scale
Specifies the scale/unit of the time window. There are three different time scales to choose from: seconds, minutes or hours.

## Output
The output event is composed of two fields. The field "value" specifies the value to count.
The second field "count" returns the number of occurrences.
Example:
```
{
  'value': 'vehicleId', 
  'count': 12
}
```