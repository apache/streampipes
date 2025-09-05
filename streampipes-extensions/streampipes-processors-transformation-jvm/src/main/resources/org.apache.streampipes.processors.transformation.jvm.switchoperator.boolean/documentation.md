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

# Switch Operator (Boolean Input)

<p> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

## Description

The `Switch Operator (Boolean Input)` is a StreamPipes data processor that allows you to route events based on the value of a selected boolean field. You can define multiple "switch cases," each with a specific boolean value (true or false) and a corresponding output value. If the input boolean field matches a case, the defined output value will be added to the event. A default output value is used if no case matches or an error occurs during processing.

## Configuration

### Input Stream Requirements

This processor requires an input stream with at least one boolean property.

### Output Strategy

This processor appends a new field to the event with the processed output value. The name of the output field is `switch-output`.

### Static Properties

* **Switch Field**: Select the boolean field from the input stream that will be used for the switch condition.
* **Output Type**: Choose the data type of the output value. Available options are:
    * `String`
    * `Boolean`
    * `Integer`
* **Switch Cases**: Define the different switch conditions and their corresponding output values. Each switch case consists of:
    * **Case Value**: The boolean value (`true` or `false`) to match against the selected switch field.
    * **Output Value**: The value to output if this case matches. This value will be converted to the selected `Output Type`.
* **Default Output Value**: The value to use if none of the defined switch cases match the input or if an error occurs during processing. This value will also be converted to the selected `Output Type`.

## Example

Let's say you have an event with a boolean field `isValid` and you want to output a "Status" string based on its value:

| Original Event |
| :------------- |
| `{ "isValid": true }` |

**Configuration:**

* **Switch Field**: `isValid`
* **Output Type**: `String`
* **Switch Cases**:
    * Case Value: `true`, Output Value: `Valid Data`
    * Case Value: `false`, Output Value: `Invalid Data`
* **Default Output Value**: `Unknown`

**Output Event when `isValid` is `true`:**

| Processed Event |
| :-------------- |
| `{ "isValid": true, "switch-output": "Valid Data" }` |

**Output Event when `isValid` is `false`:**

| Processed Event |
| :-------------- |
| `{ "isValid": false, "switch-output": "Invalid Data" }` |