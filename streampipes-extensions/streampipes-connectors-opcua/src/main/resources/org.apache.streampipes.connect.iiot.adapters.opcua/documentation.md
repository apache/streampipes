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

## OPC-UA Pull Adapter

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

Reads values from an OPC-UA server repeatedly

***

## Required Input

***

## Configuration

### Polling Interval

Duration of the polling interval in seconds

### Anonymous vs. Username/Password

Choose whether you want to connect anonymously or authenticate using your credentials.

&nbsp;&nbsp;&nbsp;&nbsp; **Anonymous**: No further information required <br/>
&nbsp;&nbsp;&nbsp;&nbsp; **Username/Password**: Insert your `username` and `password` to access the OPC UA server

### OPC UA Server

Where can the OPC UA server be found?

&nbsp;&nbsp;&nbsp;&nbsp; **URL**: Specify the server's full `URL` (including port), can be with our without leading `opc.tcp://`<br/>
&nbsp;&nbsp;&nbsp;&nbsp; **Host/Port**: Insert the `host` address (with or without leading `opc.tcp://`) and the `port`<br/>

### Namespace Index

Requires the index of the namespace you want to connect to.

### Node ID

The identifier of the node you want to read from, numbers and strings are both valid.

### Available Nodes

Shows all available nodes once namespace index and node ID are given.
Select as much as you like to query.

***
