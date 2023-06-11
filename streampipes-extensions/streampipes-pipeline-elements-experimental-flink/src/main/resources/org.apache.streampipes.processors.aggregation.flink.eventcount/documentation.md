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

## Event Counter



***

## Description
Counts the number of events arriving within a time window. An event is emitted every time the time window expires.

***

## Required input
There is no specific input required.

***

## Configuration
Time Window: The scale and size of the time window.

### TimeWindowSize  
Specifies the size of the time window.

### Time Window Scale
Specifies the scale/unit of the time window. There are three different time scales to choose from: seconds, minutes or hours.

## Output
```
{
  'timestamp': 1601301980014, 
  'count': 12
}
```