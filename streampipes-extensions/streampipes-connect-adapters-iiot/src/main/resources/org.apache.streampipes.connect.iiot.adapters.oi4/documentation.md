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

# Open Industry 4.0 (OI4)

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

---

## Description

The OI4 adapter facilitates the integration of any OT-device compliant with the OI4 standard into Apache StreamPipes.
For detailed information about this standard, please refer to their [development guide](https://openindustry4.com/fileadmin/Dateien/Downloads/OEC_Development_Guideline_V1.1.1.pdf).

### Requirements

Your OI4-compatible device should emit data via an MQTT broker.

### Restrictions

This adapter exclusively allows data consumption from a specific MQTT topic.
If you have different requirements, please notify us through the mailing list or GitHub discussions.

---

## Configuration

Below is a list of the configuration parameters you need to provide.

### Broker URL

Enter the URL of the broker, including the protocol and port number (e.g., `tcp://10.20.10.3:1883`).

### Access Mode

Choose between unauthenticated access or input your credentials for authenticated access.

### Sensor Description

You should provide information about the sensor you want to connect to. This can be achieved in two ways:

a) **By Type**: Specify the type of sensor you want to connect to, e.g., `'VVB001'`. <\br>
b) **By IODD**: Simply upload the IODD description of the respective sensor. Please note: This feature is not yet available! If you're interested in this feature, please notify us through the mailing list or GitHub discussions and share your use case with us.

### Selected Sensors

Configure which sensors of the master device you want to connect to. You can either select `All`, which will provide data from all sensors available on the respective MQTT topic, or choose `Custom Selection` and provide a list of sensor IDs in a comma-separated string (e.g., `000008740649,000008740672`).

## Output

The output consists of all values from the selected sensor type. Below is an example for the `VVB001 sensor`:

```json
{
    "a-Rms": 1.8,
    "OUT2": true,
    "SensorID": "000008740649",
    "Temperature": 22,
    "Crest": 3.7,
    "v-Rms": 0.0023,
    "OUT1": true,
    "Device status": 0,
    "timestamp": 1685525380729
}
```
