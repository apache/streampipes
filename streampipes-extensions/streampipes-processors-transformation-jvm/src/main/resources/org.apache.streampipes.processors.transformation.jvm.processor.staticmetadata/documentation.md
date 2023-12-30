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
## Static Metadata Enricher

Enrich a data stream by dynamically adding fields based on user-provided static metadata configuration.

---

## Description

The Static Metadata Enricher is designed to enrich a data stream by dynamically adding fields based on user-provided
metadata configuration. Users can specify static properties, and the processor will process each event, adding fields
according to the provided key-value pairs. The output strategy is determined dynamically based on the provided metadata.
For added convenience, users also have the option of uploading a CSV file with metadata information.

### Configuration

For each metadata entry, configure the following three options:

- **Runtime Name:** A unique identifier for the property during runtime.
- **Value:** The value associated with the property.
- **Data Type:** The data type of the property value.

#### Using CSV Option

Alternatively, you can utilize the CSV upload feature by creating a CSV file with the following format:

```
Runtime Name,Runtime Value,Data Type
sensorType,Temperature,String
maxSensorValue,100.0,Float
minSensorValue,0,Float
```

## Example
### Input Event

```json
{
  "reading": 25.5
}
```

### Output Event

```json
{
  "reading": 25.5,
  "sensorType": "Temperature",
  "maxSensorValue": 100.0,
  "minSensorValue": 0.0
}
```
