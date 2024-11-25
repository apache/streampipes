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

This adapter reads node values from an OPC-UA server.
The adapter supports both signed/encrypted and unencrypted communication.

Certificates must be provided directly to the service and cannot be added from the UI or REST APIs.
To establish connections using a `Sign` or `Sign & Encrypt` security mode, 
the following environment variables must be provided to the extension service:

* SP_OPCUA_SECURITY_DIR the directory where the keystore and trusted certificates are located
* SP_OPCUA_KEYSTORE_FILE the keystore file (e.g., keystore.pfx, must be of type PKCS12)
* SP_OPCUA_KEYSTORE_PASSWORD the password to the keystore
* SP_OPCUA_APPLICATION_URI the application URI used by the client to identify itself

Certificate requirements:

The X509 certificate must provide the following extras:
* Key Usage: Certificate Sign
* Subject Alternative Name: Application URI
* Basic Constraints: Must provide CA:FALSE when using a self-signed certificate
* Extended Key Usage: TLS Web Server Authentication, TLS Web Client Authentication

The directory layout of the `SP_OPCUA_SECURITY_DIR` look as follows:

```
SP_OPC_SECURITY_DIR/
├─ pki/
│  ├─ issuers/
│  ├─ rejected/
│  ├─ trusted/
│  │  ├─ certs/
│  │  ├─ crl/
```

Trusted certs need to be present in the `pki/trusted/certs` folder.
Rejected certificates are stored in the `rejected` folder.

***

## Required Input

***

## Configuration

### Polling Interval

Duration of the polling interval in seconds

### Security Mode

Can be either None, Signed or Signed & Encrypt

### Security Policy

Choose one of the OPC-UA security policies or `None`

### User Authentication

Choose whether you want to connect anonymously or authenticate using your credentials.

&nbsp;&nbsp;&nbsp;&nbsp; **Anonymous**: No further information required <br/>
&nbsp;&nbsp;&nbsp;&nbsp; **Username/Password**: Insert your `username` and `password` to access the OPC UA server

### OPC UA Server

Where can the OPC UA server be found?

&nbsp;&nbsp;&nbsp;&nbsp; **URL**: Specify the server's full `URL` (including port), can be with our without leading `opc.tcp://`<br/>
&nbsp;&nbsp;&nbsp;&nbsp; **Host/Port**: Insert the `host` address (with or without leading `opc.tcp://`) and the `port`<br/>

### Available Nodes

Shows all available nodes once namespace index and node ID are given.
Select as much as you like to query.

***
