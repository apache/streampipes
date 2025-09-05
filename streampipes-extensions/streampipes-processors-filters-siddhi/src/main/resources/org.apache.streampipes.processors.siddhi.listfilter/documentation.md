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

## List Filter

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description
The List Filter processor filters events based on the presence of a specific value in a list field. It:
* Checks if a value exists in a list
* Filters events based on list membership
* Preserves original event data
* Works with any list field type
* Supports exact value matching

***

## Required Input
The processor requires an input event stream with at least one list field to filter on.

***

## Configuration

### List Field
Select the list field to check for the required value. The field must be a list type.

### Required Value
Specify the value to look for in the list. The processor will only output events where this value is present in the selected list field.

## Output
The processor outputs only those events where the specified value is found in the selected list field.

### Example

#### Input Event
```json
{
  "sensor_id": "sensor1",
  "measurements": [22.1, 23.4, 24.2, 25.0, 25.5]
}
```

#### Configuration
* List Field: `measurements`
* Required Value: `25.0`

#### Output Event
The processor will output the event because 25.0 is present in the measurements list.

## Use Cases

1. **Data Filtering**
   * Filter events by list membership
   * Select specific value occurrences
   * Filter based on value presence
   * Create value-based subsets

2. **Event Selection**
   * Select events with specific values
   * Filter based on value existence
   * Create value-based event streams
   * Implement value-based routing

3. **Quality Control**
   * Filter valid measurements
   * Select events with expected values
   * Filter based on value criteria
   * Implement value-based validation

## Notes

* The processor performs exact value matching
* Original event data is preserved
* The processor works with any list field type
* Events are only output if the value is found
* List order does not affect filtering