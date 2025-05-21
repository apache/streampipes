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

## Text Filter

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description
The Text Filter processor filters events based on text field content. It allows you to:
* Match exact text strings
* Check for text containment
* Filter events based on text criteria
* Route events based on text content

***

## Required Input
The processor requires an input event stream containing at least one text field to filter on.

***

## Configuration

### Text Field
Select the field containing the text that should be filtered.

### Operation
Choose from two filtering operations:
* **MATCHES**: Exact string matching (case-sensitive)
* **CONTAINS**: Substring matching (case-sensitive)

### Keyword
Specify the text string to match against.

## Output
The processor forwards the input event only if the text field satisfies the filter condition.

### Example

#### Input Event
```json
{
  "message": "Temperature warning: 25.5°C",
  "timestamp": 1586380104915
}
```

#### Configuration
* Text Field: `message`
* Operation: `CONTAINS`
* Keyword: `warning`

#### Output Event
```json
{
  "message": "Temperature warning: 25.5°C",
  "timestamp": 1586380104915
}
```

## Use Cases

1. **Event Routing**
   * Route events based on text content
   * Filter log messages
   * Process specific error messages
   * Handle different event types

2. **Content Filtering**
   * Filter text-based alerts
   * Process specific keywords
   * Extract relevant messages
   * Filter notifications

3. **Data Validation**
   * Validate text content
   * Ensure required text patterns
   * Filter invalid messages
   * Enforce text standards

## Notes

* Text matching is case-sensitive
* The processor preserves the original event structure
* No text transformation is performed
* Events that don't match the filter are dropped
* The filter operates on the exact text field value