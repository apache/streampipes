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

## Field Mapper

<p align="center">
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

The Field Mapper processor combines multiple fields into a single field by computing an MD5 hash value of their combined contents. This processor is particularly useful for:

* Creating unique identifiers from multiple fields
* Data anonymization and privacy protection
* Reducing data dimensionality
* Generating consistent keys for data grouping
* Combining related fields into a single reference

***

## Required input

The processor can work with any event that contains at least one field. The fields to be mapped can be of any data type, as they will be converted to their string representation before hashing.

***

## Configuration

### Fields

Select one or more fields that should be combined into a single hash value. The order of field selection matters, as it affects the resulting hash value.

### New Field Name

Specify the name of the new field that will contain the MD5 hash value of the combined fields.

## Output

The processor creates a new event that:
* Retains all fields from the input event that were not selected for mapping
* Adds a new field with the specified name containing the MD5 hash of the combined selected fields
* Removes the original fields that were mapped

### Example

#### Input Event
```json
{
  "timestamp": 1586380104915,
  "mass_flow": 4.3167,
  "temperature": 40.05,
  "pressure": 1013.25,
  "sensorId": "flowrate01"
}
```

#### Configuration
* Fields: mass_flow, temperature
* New Field Name: combined_measurement

#### Output Event
```json
{
  "timestamp": 1586380104915,
  "pressure": 1013.25,
  "sensorId": "flowrate01",
  "combined_measurement": "8ae11f5c83610104408d485b73120832"
}
```

## Use Cases

1. **Data Privacy**
   * Combine personally identifiable information (PII) into anonymized identifiers
   * Create privacy-preserving keys for data linkage
   * Generate pseudonyms for sensitive data

2. **Data Integration**
   * Create composite keys for data joining
   * Generate unique identifiers across systems
   * Standardize multi-field references

3. **Data Quality**
   * Track changes across multiple fields
   * Create checksums for data validation
   * Monitor data integrity

4. **Performance Optimization**
   * Reduce storage requirements by combining fields
   * Optimize indexing with combined keys
   * Improve query performance with single-field lookups

## Notes

* The hash value is deterministic - the same input fields will always produce the same hash
* The hash is irreversible - you cannot recover the original field values from the hash
* Field order matters - changing the order of fields will produce a different hash
* All field values are converted to strings before hashing
* The output hash is always a 32-character hexadecimal string
* Null or empty field values are handled gracefully but will affect the resulting hash