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

## ifm IOLink

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

This adapter enables the integration of IO-Link sensor data produced by a Hilscher IO-Link Master with Apache StreamPipes.
For detailed instructions, please refer to the Hilscher documentation.

### Requirements
tbc

### Restrictions
This version supports a single IO-Link master. If you want to connect multiple masters, they must have the same setup.
If you have different requirements, please inform us through the mailing list or GitHub discussions.

***

## Configuration

Here is a list of the configuration parameters you must provide.

### Broker URL

Enter the URL of the broker, including the protocol (e.g. `tcp://10.20.10.3:1883`)

### Access Mode

If necessary, provide broker credentials.

### Sensor Type

Choose the type of sensor you want to connect. (**IMPORTANT:** Currently, only the VVB001 is supported)

## Output

The output includes all values from the selected sensor type. Here is an example for the `VVB001 sensor`:
```
{
    "a-Rms": 1.8,
    "OUT2": true,
    "SensorID": "000008740649",
    "Temperature": 22,
    "Crest": 3.7,
    "v-Rms": 0.0023,
    "OUT1": true,
    "Device status": 0,
    "a-Peak": 6.6,
    "timestamp": 1685525380729
}
```
