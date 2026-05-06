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

The WinCC Alarm Archive adapter reads Siemens WinCC alarm archives exported as CSV files.

It is intended for the WinCC alarm CSV structure and for both archive modes:

- single archive file
- segmented circular log with multiple rotating CSV segments

Configuration is Siemens-specific:

- archive directory
- archive base name
- segmented circular log enabled or disabled
- segment count in segmented circular log mode
- polling interval
- optional delay between replayed events in milliseconds

The adapter assumes the WinCC alarm CSV layout with a header row and semicolon-separated columns.
The configured base name should not include the segment number or file suffix. For example, use `Meldungsarchiv`
to match `Meldungsarchiv1.csv` or `Meldungsarchiv1.csv` to `MeldungsarchivN.csv`.

In segmented circular log mode the adapter treats reused file names as new generations by fingerprinting file content,
so a segment like `Meldungsarchiv1.csv` can be consumed again after WinCC rotates the archive.

Use the inter-event delay when consumers or brokers cannot handle large replay bursts. A value like `1` ms can reduce
event loss on slower consumers, while `0` disables throttling.

The configured directory must be reachable from the extension runtime process or container.
