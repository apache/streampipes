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

## Microsoft SQL Server CDC Adapter

***

## Description

This adapter uses the embedded Debezium Engine to capture change events from a CDC-enabled Microsoft SQL Server table.

Current behavior:

* only newly inserted rows are emitted
* updates and deletes are ignored
* the output event is flat and contains only the row fields
* Debezium metadata is not included in emitted events
* adapter state is kept in memory only

This means the adapter starts at the current log position and does not read old rows when it starts.
If the adapter is stopped, its position is lost and rows written while it is down can be missed.

***

## Required Input

None.

***

## SQL Server Prerequisites

The adapter expects the following SQL Server setup:

* CDC is enabled on the selected database
* CDC is enabled on the selected table
* SQL Server Agent is running
* the configured SQL Server user can access the database metadata and captured CDC data

The configuration dialog only lists CDC-enabled tables from the selected database.

***

## Configuration

### Host

The SQL Server host name or IP address.

### Port

The SQL Server TCP port, for example `1433`.

### Database

The name of the CDC-enabled SQL Server database.

### Username / Password

Credentials used to connect to SQL Server.

### Encrypt Connection

Enable TLS for the SQL Server connection.

### Trust Server Certificate

Trust the SQL Server certificate without validating its chain.
This is useful for local or test environments with self-signed certificates.

### Timezone

Timezone used for SQL Server temporal types without embedded timezone information, such as `DATETIME` and `DATETIME2`.

Use an IANA timezone ID such as:

* `UTC`
* `Europe/Berlin`
* `America/New_York`

The default is `UTC`.

### Table

Select one CDC-enabled table from the configured database.

***

## Output Behavior

Each insert event produces one StreamPipes event.

Example output:

```json
{
  "id": 42,
  "temperature": 23.5,
  "pressure": 1007.2,
  "createdAt": 1778154600000
}
```

Type handling:

* `DECIMAL` and `NUMERIC` values are emitted as JSON numbers
* `DATE`, `DATETIME`, `SMALLDATETIME`, `DATETIME2`, and `DATETIMEOFFSET` values are normalized to epoch milliseconds
* `TIME` values are normalized to milliseconds past midnight
* binary values are emitted as Base64 strings

Runtime polling:

* Debezium polling is controlled through the environment variable `SP_CDC_MSSQL_POLL_INTERVAL_MS`
* the default polling interval is `1000` milliseconds

***

## Limitations

Current limitations:

* single-table capture only
* insert events only
* no snapshot of existing rows
* no offset persistence across adapter restarts
* no schema-history persistence across adapter restarts
* timestamp semantics for downstream sinks still depend on the field selection in StreamPipes
