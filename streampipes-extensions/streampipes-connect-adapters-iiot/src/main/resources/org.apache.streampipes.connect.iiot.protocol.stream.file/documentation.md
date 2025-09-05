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

## File (Stream)

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

The File Stream Adapter enables continuous streaming of file contents to Apache StreamPipes, creating a data stream for utilization within StreamPipes. It's particularly handy when you prefer not to connect directly to the data source via StreamPipes or for testing and demonstration purposes. Currently, it supports the following file types:

- CSV
- JSON
- XML

### Example

Suppose we have a CSV file (`temperature.csv`) containing data from a temperature sensor recording data every second:

```text
time,temperature
1715593295000,36.3
1715593296000,37.5
1715593297000,37.0
1715593298000,37.2
1715593299000,37.2
1715593210000,37.6
1715593211000,37.4
1715593212000,37.5
1715593213000,37.5
1715593214000,37.7
```

When creating a new File Stream Adapter:
- Upload the file
- Select `yes` for `Replay Once`
- Choose `CSV` as the `Format` with `,` as the `delimiter`, check `Header`

After creating the adapter, it will output one line of the CSV as an event every second.
Further details on configuration options are provided below.

---

## Configuration

### File

This section determines the file to be streamed by the adapter. Options include:

- `Choose existing file`: Select from files already present in StreamPipes.
- `Upload new file`: Upload a new file, also available for other adapters. Supports `.csv`, `.json`, and `.xml` file types.

### Overwrite file time
Enable this option to always pass the current system time as the timestamp when emitting an event. If your file lacks timestamp information, this should be enabled. Conversely, if your file has timestamp information, enabling this option will overwrite it with the current system time. By default, this option is disabled, leaving timestamp information unaffected.

### Replay Once
Distinguishes between replaying all data contained in the file only once or in a loop until the adapter is manually stopped.
If enabled, this will cause events from the file to be emitted multiple times. In this case, it is recommended to enable `Overwrite file time` if the resulting stream is to be persisted in StreamPipes, otherwise existing events with the same timestamp will be overwritten.

### Replay Speed

Configures the event frequency:
- **Keep original time**: Events are emitted based on the timestamp information in the file. 
- **Fastest**: All data in the file is replayed as quickly as possible, with no waiting time. 
- **Speed Up Factor**: Adjusts the waiting time of the adapter based on the provided speed up factor, considering the time between two events in the file.
