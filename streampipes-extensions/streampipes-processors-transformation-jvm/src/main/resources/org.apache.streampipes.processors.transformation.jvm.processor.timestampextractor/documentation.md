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

## Timestamp Extractor

<p align="center">
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

The Timestamp Extractor processor breaks down a timestamp into its individual time components. It supports:
* Year extraction
* Month extraction
* Day extraction
* Hour extraction
* Minute extraction
* Second extraction
* Weekday extraction
* Custom field selection
* Component isolation

This processor is essential for:
* Time analysis
* Date processing
* Time component extraction
* Temporal analysis
* Time-based grouping
* Time series analysis

***

## Required input

The processor requires a data stream containing at least one timestamp field to extract components from.

***

## Configuration

### Timestamp Field

Select the field containing the timestamp to extract components from. This should be a valid timestamp value.

### Extract Fields

Select which time components to extract:
* Year (numeric)
* Month (numeric, 1-12)
* Day (numeric, 1-31)
* Hour (numeric, 0-23)
* Minute (numeric, 0-59)
* Second (numeric, 0-59)
* Weekday (string: Monday-Sunday)

## Output

The processor creates a new event containing:
* All original fields from the input event
* New fields for each extracted time component with prefix "timestamp"
* Original timestamp preserved

### Example

#### Input Event
```json
{
  "deviceId": "sensor01",
  "timestamp": 1586380104915,
  "value": 23.5
}
```

#### Configuration
* Timestamp Field: timestamp
* Extract Fields: year, month, day, hour, minute, weekday

#### Output Event
```json
{
  "deviceId": "sensor01",
  "timestamp": 1586380104915,
  "value": 23.5,
  "timestampYear": 2020,
  "timestampMonth": 4,
  "timestampDay": 8,
  "timestampHour": 15,
  "timestampMinute": 35,
  "timestampWeekday": "Wednesday"
}
```

## Use Cases

1. **Time Analysis**
   * Extract time components
   * Analyze patterns
   * Group by time
   * Track changes
   * Build time series

2. **Data Processing**
   * Process timestamps
   * Extract components
   * Analyze patterns
   * Group data
   * Build metrics

3. **Reporting**
   * Generate time reports
   * Extract components
   * Analyze patterns
   * Group data
   * Build summaries

4. **Monitoring**
   * Monitor time patterns
   * Extract components
   * Analyze trends
   * Track changes
   * Build alerts

## Notes

* Timestamp must be valid
* Components are optional
* Processing is stateless
* Multiple components supported
* Extraction is immediate
* No delay in processing
* Original timestamp preserved
* Components are standardized
* Weekday is returned as string
* Month is 1-based (1-12)
* Hour is 24-hour format (0-23)