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

<p align="center">
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

The Static Metadata Enricher processor adds predefined metadata fields to events. It supports:
* Custom field addition
* Multiple data types (String, Boolean, Float, Integer)
* Field labeling
* Field descriptions
* Runtime naming
* Metadata enrichment

This processor is essential for:
* Adding context to data
* Enriching events
* Creating metadata
* Building annotations
* Standardizing fields
* Documenting data

***

## Required input

The processor requires a data stream to enrich with additional metadata fields.

***

## Configuration

### Metadata Input

Configure the metadata fields to add to each event:

#### Runtime Name
Enter the name that will be used for the field in the output event.

#### Runtime Value
Enter the value that will be assigned to the field.

#### Data Type
Select the data type of the value:
* String: Text values
* Boolean: true/false values
* Float: Decimal numbers
* Integer: Whole numbers

#### Label (Optional)
Provide a short label describing the field.

#### Description (Optional)
Provide a detailed description of the field.

## Output

The processor creates a new event containing:
* All original fields from the input event
* The configured metadata fields with their values

### Example

#### Input Event
```json
{
  "deviceId": "sensor01",
  "temperature": 23.5,
  "humidity": 45.2
}
```

#### Configuration
* Runtime Name: location
* Runtime Value: Building A
* Data Type: String
* Label: Sensor Location
* Description: Physical location of the sensor

#### Output Event
```json
{
  "deviceId": "sensor01",
  "temperature": 23.5,
  "humidity": 45.2,
  "location": "Building A"
}
```

## Use Cases

1. **Data Enrichment**
   * Add context to data
   * Enrich events
   * Create metadata
   * Build annotations
   * Standardize fields

2. **Documentation**
   * Document data
   * Add descriptions
   * Create labels
   * Build metadata
   * Standardize fields

## Notes

* Multiple fields can be added
* Data types must match values
* Labels are optional
* Descriptions are optional
* Processing is stateless
* Field names must be unique
* Values are automatically cast to the selected data type
* Original event fields are preserved
* Metadata is consistent across all events
