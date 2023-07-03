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

This adapter enables the integration of IO-Link sensor data produced by an ifm IO-Link Master
(e.g., AL1350) with Apache StreamPipes. To use this adapter, you need to configure your IO-Link
master to publish events to an MQTT broker. This can be achieved through a REST interface or via
the browser at `http://##IP_OF_IO_LINK_MASTER##/web/subscribe`. For detailed instructions,
please refer to the ifm documentation.

### Requirements
The JSON events should include the following information:
- `deviceinfo.serialnumber`
- Only the pdin value is required for each port (e.g., `port[0]`).
- The event `timer[1].datachanged` can be used as a trigger.
Using this adapter, you can create a stream for sensors of the same type.

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

### Ports

Select the ports that are connected to the IO-Link sensors.

### Sensor Type

Choose the type of sensor you want to connect. (**IMPORTANT:** Currently, only the VVB001 is supported)

## Output

The output includes all values from the selected sensor type. Here is an example for the `VVB001 sensor`:
```
{
    "aPeak": 6.6,
    "aRms": 1.8,
    "crest": 3.7,
    "out1": true,
    "out2": true,
    "port": "000000001234",
    "status": 0,
    "temperature": 22,
    "timestamp": 1685525380729,
    "vRms": 0.0023
}
```
