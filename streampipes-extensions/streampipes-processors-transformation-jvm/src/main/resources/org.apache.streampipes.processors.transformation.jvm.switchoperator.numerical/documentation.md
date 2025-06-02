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

# Switch Operator Processor

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

## Example

This example demonstrates using the Switch Operator to evaluate a device status field with a Boolean output:
1. Select "deviceStatus" as the input field to monitor.
2. Choose "Boolean" as the output type.
3. Configure a switch case to match the value "ONLINE" and return true.
4. Set the default output value to false for all other values.
5. The processor outputs true when the device is online, and false otherwise.

For example, when the input event is:
```json
{
"deviceId": "pump-1",
"deviceStatus": "ONLINE",
"timestamp": 1716930475000
}
```

The output will be:
```json
{
   "deviceId": "pump-1",
   "deviceStatus": "ONLINE",
   "timestamp": 1716930475000,
   "switch-filter-result": true
}
```

Alternatively, if the output type is set to "String" with a case mapping "ONLINE" to "ACTIVE":
```json
{
   "deviceId": "pump-1",
   "deviceStatus": "ONLINE",
   "timestamp": 1716930475000, 
   "switch-filter-result": "ACTIVE"
}
```


## Description

The Switch Operator processor evaluates the value of a selected input field against a set of predefined cases and produces an output based on the matching case, with a user-selectable data type (String, Boolean, or Integer). It functions like a switch-case statement in programming languages, allowing flexible conditional logic.
This processor is useful for:

- Converting field values to specific outputs (e.g., status strings to boolean flags or numeric codes).
- Implementing conditional logic in data pipelines.
- Triggering different pipeline branches based on field values.
- Creating typed outputs for downstream processors or dashboards.

The processor forwards all events, adding a result field with the outcome of the evaluation in the chosen data type.

## Configuration
The Switch Operator requires the following configuration:

1. **Input Field** - Select the field from the input event to evaluate. Any data type is supported, and the value is converted to a string for comparison.Input Field - Select the field from the input event to evaluate. Any data type is supported, with the value converted to a string for comparison.
2. **Output Type** - Choose the data type for the result field: String, Boolean, or Integer.
3. **Switch Cases** - Define one or more case-value pairs:
- **Case Value** - The string value to match against the input field.
- **Output Value** - The value to return when the case matches, corresponding to the selected output type (e.g., true/false for Boolean, any string for String, a number for Integer).
4. **Default Output Value** - The value to return when no cases match, based on the output type:
- String: Empty string ("").
- Boolean: false.
- Integer: 0.

Note: If the input field is missing, null, or causes an error, the default output value for the selected type is used.

## Output
The processor forwards all incoming events and adds a new field:

- **switch-filter-result** - A field of the user-selected type (String, Boolean, or Integer) based on the case matching result.

For example, with a Boolean output type and an "ON" match returning `true`, an event with a "status" field of "ON" will include `switch-filter-result: true`. For a String output type mapping "ON" to "RUNNING", the output will include `switch-filter-result: "RUNNING"`.