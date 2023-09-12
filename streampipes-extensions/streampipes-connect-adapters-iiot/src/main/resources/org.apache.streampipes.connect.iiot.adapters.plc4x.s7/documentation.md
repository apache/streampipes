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

## Siemens S7

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

The adapter allows to connect with a Siemens S7 PLC.

***

## Configuration

The following configuration options are available when creating an adapter:

### PLC Address

This field requires the PLC address in form of the IP without the prefixed protocol (e.g., 192.168.34.56).

In addition to the pure IP, other parameters supported by Apache PLC4X can be provided as an URL parameter:

* `local-rack`
* `local-slot`
* `local-tsap`
* `remote-rack`
* `remote-slot`

Additional configs are separated by `&`.

Example address: `192.68.34.56?remote-rack=0&remote-slot=3&controller-type=S7_400`

See the <a href="https://plc4x.apache.org/users/protocols/s7.html">Apache PLC4X documentation</a> for more information.

### Polling Interval

The polling interval requires a number in milliseconds, which represents the interval in which the adapter will poll the
PLC for new data. For instance, a polling interval of 1000 milliseconds will configure the adapter to send a request to
the PLC every second.

### Nodes

In the Nodes section, the PLC nodes that should be gathered are defined.
There are two options to define the nodes:

* Manual configuration: The address must be assigned manually by providing a runtime name, the node name and the
  datatype. The `Runtime Name` will be the StreamPipes-internal name of the field, which will also show up in the data
  explorer and pipeline editor. The `Node Name` refers to the node address of the PLC, e.g., `%Q0.4`. Finally, the data
  type can be selected from the available selection. Currently available data types
  are `Bool`, `Byte`, `Int`, `Word`, `Real`, `Char`, `String`, `Date`, `Time of Day` and `Date and Time`.
* Instead of providing the node information manually, a CSV file can be uploaded. The CSV file can, for instance, be
  exported from TIA and then be enriched with the appropriate runtime names. This is especially useful when many fields
  should be added as nodes. Here is an example export enriched with the runtime name:

```
Runtime Name,Path,Data Type,Node Name
I_High_sensor,Tag table_1,Bool,%I0.0,
I_Low_sensor,Tag table_1,Bool,%I0.1,
I_Pallet_sensor,Tag table_1,Bool,%I0.2,
I_Loaded,Tag table_1,Bool,%I0.3,
```

Note that the CSV can contain additional columns, but only the columns `Runtime Name`, `Data Type` and `Node Name` are
used, while all other columns will be ignored.

## Best Practices

Instead of creating a large event containing all nodes that should be available in StreamPipes, consider to group the
fields logically into smaller adapters.
This will ease the definition of pipelines for users and eases future modifications.
