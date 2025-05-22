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

## JavaScript Eval

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description
The JavaScript Eval processor allows you to write custom JavaScript functions to transform and enrich events. It:
* Executes user-defined JavaScript code
* Provides full access to input event data
* Supports complex data transformations
* Enables dynamic field creation and modification

***

## Required Input
The processor works with any input event stream. All fields from the input event are available as properties in the JavaScript function.

***

## Configuration

### JavaScript Function
You need to provide a JavaScript function that processes the input event and returns a new event object. The function must:
* Be named `process`
* Accept a single parameter containing the input event
* Return a map/object with the output fields

Example function structure:
```javascript
    function process(event) {
        // do processing here.
        // return a map with fields that matched defined output schema.
        return {id: event.id, tempInCelsius: (event.tempInKelvin - 273.15)};
    }
```

The defined output schema must match the event that is returned by the JavaScript function.

## Output
The processor forwards a new event containing the fields returned by your JavaScript function.

### Example

#### Input Event
```json
{
  "temperature": 25.5,
  "humidity": 60,
  "timestamp": 1586380105115
}
```

#### Configuration
```javascript
function process(event) {
    // Convert temperature from Celsius to Fahrenheit
    const tempF = (event.temperature * 9/5) + 32;
    
    // Calculate heat index
    const heatIndex = calculateHeatIndex(tempF, event.humidity);
    
    // Return new event with transformed data
    return {
        temperature_celsius: event.temperature,
        temperature_fahrenheit: tempF,
        humidity: event.humidity,
        heat_index: heatIndex,
        timestamp: event.timestamp
    };
}

function calculateHeatIndex(temp, humidity) {
    // Simplified heat index calculation
    return temp + (humidity * 0.1);
}
```

#### Output Event
```json
{
  "temperature_celsius": 25.5,
  "temperature_fahrenheit": 77.9,
  "humidity": 60,
  "heat_index": 83.9,
  "timestamp": 1586380105115
}
```

## Use Cases

1. **Data Transformation**
   * Unit conversions
   * Data normalization
   * Complex calculations
   * Field restructuring

2. **Data Enrichment**
   * Adding derived fields
   * Computing statistics
   * Combining multiple fields
   * Creating calculated metrics

3. **Custom Logic**
   * Business rules implementation
   * Conditional transformations
   * Data validation
   * Custom algorithms

## Notes

* The JavaScript function runs in a GraalVM JavaScript environment
* All input fields are accessible as properties of the event object
* The function must return a valid JavaScript object
* Error handling should be implemented in the JavaScript code
* Complex JavaScript operations are supported
* The function is executed for each incoming event, but state can be kept