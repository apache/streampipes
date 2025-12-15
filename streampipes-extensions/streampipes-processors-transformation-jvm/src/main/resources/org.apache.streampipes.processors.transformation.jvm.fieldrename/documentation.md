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

## Field Renamer

<p align="center">
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

The Field Renamer processor changes the name of fields in events. It supports:
* Single field renaming
* Custom field names
* Runtime name modification
* Field name standardization

This processor is essential for:
* Standardizing field names
* Creating consistent naming
* Mapping field identifiers
* Improving readability

***

## Required input

The processor requires a data stream containing at least one field to rename.

***

## Configuration

### Field

Select the field to rename. This field's name will be changed in the output event.

### New Field Name

Enter the new name for the selected field. This name will replace the original field name in the output event.

## Output

The processor creates a new event containing:
* All original fields from the input event
* The selected field with its new name
* All field values remain unchanged

### Example

#### Input Event
```json
{
  "deviceId": "sensor01",
  "temp": 23.5,
  "humidity": 45.2,
  "timestamp": 1586380104915
}
```

#### Configuration
* Field: temp
* New Field Name: temperature

#### Output Event
```json
{
  "deviceId": "sensor01",
  "temperature": 23.5,
  "humidity": 45.2,
  "timestamp": 1586380104915
}
```

## Use Cases

1. **Data Standardization**
   * Standardize field names
   * Create naming conventions
   * Map field identifiers
   * Improve consistency

2. **System Integration**
   * Map field names
   * Standardize interfaces
   * Create consistency
   * Build mappings

## Notes

* Only field names are changed
* Field values remain unchanged
* Original field order is preserved
* Processing is stateless
* Multiple renames require chaining