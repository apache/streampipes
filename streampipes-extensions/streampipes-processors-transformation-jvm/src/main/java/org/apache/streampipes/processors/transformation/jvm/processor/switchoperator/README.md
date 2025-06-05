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

# StreamPipes Switch Operators

This repository contains a suite of custom data processors for Apache StreamPipes, designed to enable dynamic event routing and data transformation based on specific field values. These "Switch Operators" allow users to define conditional logic to alter events, append new fields, or categorize data within a StreamPipes pipeline.

## Overview

The core idea behind these processors is to inspect a designated input field within an event and, based on predefined "switch cases," apply a transformation or assign an output value to the event. This facilitates advanced conditional processing, enabling more intelligent data flows.

The solution is built around an abstract base class (`AbstractSwitchOperatorProcessor`) to provide a common structure and shared logic, while concrete implementations handle the specifics of different data types (Boolean, Numerical, String).

## Processors Included

1.  **`SwitchOperatorBooleanInputProcessor`**: Routes events based on the `true` or `false` value of a boolean input field.
2.  **`SwitchOperatorNumericalInputProcessor`**: Routes events based on numerical comparisons (`==`, `!=`, `<`, `<=`, `>`, `>=`) against a numerical input field.
3.  **`SwitchOperatorStringInputProcessor`**: Routes events based on exact string matches against a string input field.

## Core Components and How They Work

### `AbstractSwitchOperatorProcessor.java`

This is the abstract base class for all switch operators. It defines the common structure and shared logic, minimizing code duplication across different input types.

* **Common Constants**: Defines static strings for property keys (e.g., `SWITCH_FILTER_INPUT_FIELD_KEY`, `SWITCH_CASE_GROUP_KEY`, `SWITCH_FILTER_OUTPUT_KEY`), ensuring consistent naming and easier localization.
* **Common Instance Fields**: Stores configuration parameters extracted during pipeline startup, such as `selectedSwitchField`, `selectedOutputType`, `switchCaseEntries`, and `defaultOutputValue`.
* **`onEvent(Event event, SpOutputCollector collector)`**: This is the central method called for every incoming event.
    * It calls the abstract `findMatchingResult(Event event)` method, which is implemented by the concrete processor to determine the output value based on the event's field and the defined switch cases.
    * Based on the `selectedOutputType` (String, Boolean, or Integer), it converts the `resultValue` to the appropriate type.
    * A new field, `switch-filter-result` (defined by `SWITCH_FILTER_OUTPUT_KEY`), is added to the event with the processed `resultValue`.
    * The modified event is then forwarded to the next component in the pipeline.
    * Includes `try-catch` blocks for robust type conversion (e.g., `NumberFormatException` for Integers, `IllegalArgumentException` for Booleans).
* **`getSwitchCases(IDataProcessorParameters params)`**: A helper method to parse the collection of switch case definitions provided by the user in the StreamPipes UI.
* **`parseSwitchCaseEntry(StaticPropertyExtractor staticPropertyExtractor)`**: An abstract method that forces concrete implementations to define how individual switch case entries are parsed, as this varies slightly depending on the input type (e.g., numerical cases include an operator).
* **`getDefaultResult()`**: A helper method that determines the default output value, converting the user-defined `defaultOutputValue` string into the `selectedOutputType` (String, Boolean, or Integer).

### `SwitchCaseEntry.java`

A simple POJO (Plain Old Java Object) that represents a single switch case. It holds:

* `caseValue`: The value to compare against the input field (stored as a `String`).
* `outputValue`: The value to be outputted if this case matches (stored as an `Object`).

### `NumericalSwitchCaseEntry.java`

Extends `SwitchCaseEntry` to specifically cater to numerical comparisons by adding an `operator` field.

* `operator`: Stores the logical operator (e.g., "==", "<", ">=") to be used for comparison.

### `LogicalOperator.java`

This utility class provides static methods for evaluating numerical comparisons.

* **`evaluate(String operator, double inputValue, Object compareValue)`**: Compares a `double` `inputValue` with a `compareValue` (parsed as `double`) using the specified `operator`.
* Supports various operators: `==`, `!=`, `<`, `<=`, `>`, `>=`.
* Handles `IllegalArgumentException` for unknown operators.

## Specific Processor Implementations

Each concrete processor extends `AbstractSwitchOperatorProcessor` and provides specific implementations for `declareConfig()`, `onPipelineStarted()`, `parseSwitchCaseEntry()`, `findMatchingResult()`, and `onPipelineStopped()`.

### `SwitchOperatorBooleanInputProcessor.java`

* **`declareConfig()`**: Defines the UI configuration for a boolean switch. It requires a boolean input stream property and allows users to define "true" or "false" cases with corresponding output values.
* **`parseSwitchCaseEntry()`**: Parses the `caseValue` and `outputValue` from the static properties, creating a `SwitchCaseEntry`.
* **`findMatchingResult()`**: Retrieves the boolean value from the event and iterates through the `switchCaseEntries`. It uses `Boolean.parseBoolean()` to compare the event's field value with the `caseValue` of each entry.

### `SwitchOperatorNumericalInputProcessor.java`

* **`declareConfig()`**: Defines the UI configuration for a numerical switch. It requires a numerical input stream property and includes options for selecting an operator for each case.
* **`parseSwitchCaseEntry()`**: Parses the `caseValue`, `operator`, and `outputValue`, creating a `NumericalSwitchCaseEntry`.
* **`findMatchingResult()`**: Retrieves the numerical value from the event. It then uses the `LogicalOperator.evaluate()` method to compare the event's field value with the `caseValue` of each `NumericalSwitchCaseEntry` using the specified `operator`.

### `SwitchOperatorStringInputProcessor.java`

* **`declareConfig()`**: Defines the UI configuration for a string switch. It requires a string input stream property.
* **`parseSwitchCaseEntry()`**: Parses the `caseValue` and `outputValue` from the static properties, creating a `SwitchCaseEntry`.
* **`findMatchingResult()`**: Retrieves the string value from the event and iterates through the `switchCaseEntries`. It uses the `equals()` method for direct string comparison.

### `IStreamPipesSwitchProcessor.java`

A simple marker interface that `AbstractSwitchOperatorProcessor` implements, indicating that these classes are StreamPipes Data Processors.

## Output

All switch operators append a new field named `switch-filter-result` to the outgoing event. The data type of this new field is determined by the `Output Type` selected in the processor's configuration (`String`, `Boolean`, or `Integer`).

## Deployment and Usage

These processors are designed to be deployed as extensions within an Apache StreamPipes environment. Once deployed, they will appear in the StreamPipes UI under the "Processors" category and can be dragged-and-dropped into pipelines to perform conditional data transformations.

## File Structure
```plaintext  
.
├── AbstractSwitchOperatorProcessor.java
├── IStreamPipesSwitchProcessor.java
├── LogicalOperator.java
├── NumericalSwitchCaseEntry.java
├── SwitchCaseEntry.java
├── SwitchOperatorBooleanInputProcessor.java
├── SwitchOperatorNumericalInputProcessor.java
└── SwitchOperatorStringInputProcessor.java
```