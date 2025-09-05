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

## Modbus

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

The Modbus adapter allows to connect to a PLC using the Modbus specification.

***

## Configuration

The following configuration options are available when creating the adapter:

### PLC Address

The IP address of the Modbus device without any prefix, which will be added automatically when creating the adapter.

### PLC Port

The PLC port refers to the port of the PLC, such as 502.

### Node ID

The Node ID refers to the ID of the specific device.

### Nodes

The `Nodes` section requires configuration options for the individual nodes.
Nodes can be either imported from a comma-separated CSV file, or can be directly assigned in the configuration menu.

The following fields must be provided for each node:

* Runtime Name: Refers to the field to internally identify the node, e.g., in the data explorer or pipeline editor.
* Node Address: Refers to the address of the Node in Modbus, e.g., 1
* Object Type: Can be selected from the available options `DiscreteInput`, `Coil`, `InputRegister`,
  or `HoldingRegister`. 

An example CSV file looks as follows:

```
Runtime Name,Node Address,Object Type,
field1,1,Coil
temperature,2,Coil
```

Note that the CSV header must exactly match the titles `Runtime Name`, `Node Address` and `Object Type`.
