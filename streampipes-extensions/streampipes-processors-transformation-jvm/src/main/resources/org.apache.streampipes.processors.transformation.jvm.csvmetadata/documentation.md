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

## CSV Metadata Enricher

<p align="center">
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

The CSV Metadata Enricher processor adds additional fields to events by looking up values in a CSV file. It supports:
* CSV file integration
* Field mapping
* ID-based lookups
* Multiple field enrichment
* Static metadata

This processor is essential for:
* Adding static metadata
* Enriching event data
* Mapping reference data
* Creating data relationships

***

## Required input

The processor requires:
* A data stream containing an ID field for lookups
* A CSV file containing the metadata to be added
* A field in the CSV file that matches the ID field

***

## Configuration

### ID Field

Select the field from the input event that will be used to look up matching records in the CSV file.

### CSV File

Upload the CSV file containing the metadata to be added to the events. The file should contain:
* A column that matches the ID field
* Additional columns with the metadata to be added

### Field to Match

Select the column name in the CSV file that corresponds to the ID field. This field will be used to find matching records.

### Fields to Append

Select one or more columns from the CSV file that should be added to the events. These fields will be appended to the output events.

## Output

The processor creates a new event containing:
* All original fields from the input event
* The selected fields from the CSV file for matching records

### Example

#### Input Event
```json
{
  "deviceId": "sensor01",
  "temperature": 23.5,
  "timestamp": 1586380104915
}
```

#### CSV File Content
```csv
device_id,location,manufacturer,model
sensor01,Building A,Acme Corp,TempPro 2000
sensor02,Building B,Acme Corp,TempPro 2000
```

#### Configuration
* ID Field: deviceId
* Field to Match: device_id
* Fields to Append: location, manufacturer, model

#### Output Event
```json
{
  "deviceId": "sensor01",
  "temperature": 23.5,
  "timestamp": 1586380104915,
  "location": "Building A",
  "manufacturer": "Acme Corp",
  "model": "TempPro 2000"
}
```

## Use Cases

1. **Device Management**
   * Add device metadata
   * Map locations
   * Track equipment info
   * Monitor assets

2. **Data Enrichment**
   * Add reference data
   * Map relationships
   * Create context
   * Build hierarchies

## Notes

* CSV file must be uploaded
* ID field must exist in both event and CSV
* Field names are case-sensitive
* CSV must have a header row
* No matches return empty values
* Processing is stateless
* CSV is loaded at startup
