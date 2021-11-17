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

## Stream Stop Detection

***

## Description

Triggers an event when the input data stream stops sending events

***

## Required input

Does not have any specific input requirements.

***

## Configuration

### Time Window Length (Seconds)

Specifies the size of the time window in seconds.

## Output

Outputs a similar event like below.

```
{
  'timestamp': 1621243855401,
  'message': 'Event stream has stopped'  
}
```