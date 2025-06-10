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
# Switch Operator (Numerical Input)
<p> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

## Description

The `Switch Operator (Numerical Input)` is a StreamPipes data processor that allows you to route events based on the numerical value of a selected field. You can define multiple "switch cases," each with a numerical value, an operator (e.g., `==`, `!=`, `<`, `<=`, `>`, `>=`), and a corresponding output value. If the input numerical field matches a case based on the specified operator, the defined output value will be added to the event. A default output value is used if no case matches or an error occurs during processing.

## Configuration

### Input Stream Requirements

This processor requires an input stream with at least one numerical property.

### Output Strategy

This processor appends a new field to the event with the processed output value. The name of the output field is `switch-output`.

### Static Properties

* **Switch Field**: Select the numerical field from the input stream that will be used for the switch condition.
* **Output Type**: Choose the data type of the output value. Available options are:
    * `String`
    * `Boolean`
    * `Integer`
* **Switch Cases**: Define the different switch conditions and their corresponding output values. Each switch case consists of:
    * **Case Value**: The numerical value to compare against the selected switch field.
    * **Operator**: The logical operator to use for the comparison (e.g., `==` (equals), `!=` (not equals), `<` (less than), `<=` (less than or equals), `>` (greater than), `>=` (greater than or equals)).
    * **Output Value**: The value to output if this case matches. This value will be converted to the selected `Output Type`.
* **Default Output Value**: The value to use if none of the defined switch cases match the input or if an error occurs during processing. This value will also be converted to the selected `Output Type`.

## Example

Let's say you have an event with a numerical field `temperature` and you want to output a "Status" string based on its value:

| Original Event |
| :------------- |
| `{ "temperature": 25.5 }` |

**Configuration:**

* **Switch Field**: `temperature`
* **Output Type**: `String`
* **Switch Cases**:
    * Case Value: `20`, Operator: `<=`, Output Value: `Cold`
    * Case Value: `30`, Operator: `>`, Output Value: `Hot`
    * Case Value: `20`, Operator: `>`, Output Value: `Moderate`
* **Default Output Value**: `Unknown`

**Output Event when `temperature` is `15`:**

| Processed Event |
| :-------------- |
| `{ "temperature": 15.0, "switch-output": "Cold" }` |

**Output Event when `temperature` is `28`:**

| Processed Event |
| :-------------- |
| `{ "temperature": 28.0, "switch-output": "Moderate" }` |

**Output Event when `temperature` is `35`:**

| Processed Event |
| :-------------- |
| `{ "temperature": 35.0, "switch-output": "Hot" }` |
