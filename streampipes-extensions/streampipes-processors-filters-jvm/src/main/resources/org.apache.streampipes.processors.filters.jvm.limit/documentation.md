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

## Rate Limit

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description
This limits the number of events emitted based on a specified criterion such as time, and number of events.

***

## Required input
The processor works with any input event.

***

## Configuration

### Enable Grouping
Enabling this will use grouping with rate-limiting (note: disabling this will ignore `Grouping Field` property).

### Grouping Field
Runtime field to be used as the grouping key. If grouping is disabled, this setting will be ignored.

### Window Type
This specifies the type of window to be used (time / length / cron).

### Length Window Size
Length window size in event count (note: only works with length window type).

### Time Window Size
Time window size in milliseconds (note: only works with time window type).

### Cron Window Expression
Cron expression [Link](https://www.freeformatter.com/cron-expression-generator-quartz.html) to trigger and emit events (i.e `0 * * ? * *` for every minute) (note: only works with cron window type).

### Output Event Selection
This specifies the event(s) that are selected to be emitted.
- First: emit first event of the window.
- Last: emit last event of the window.
- All: emit all events of the window.

## Output
The processor outputs events which satisfies rate-limiting conditions.
