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

## Microsoft SQL Server Table Polling Adapter

This adapter polls appended rows from one ordinary SQL Server table. It does not use Debezium and does not require
Change Data Capture (CDC).

### Source requirements

Select a non-null `TINYINT`, `SMALLINT`, `INT`, `BIGINT`, or scale-zero `DECIMAL`/`NUMERIC` sequence column backed by
a single-column unique constraint. The table must be append-only and sequence values must become visible in increasing
order. Updates, deletes, views, joins, and late lower-sequence commits are not supported.

### Startup behavior

* **New rows only** records the current maximum when state is absent.
* **All existing rows** reads the table from its first sequence value.
* **Custom sequence** reads rows whose sequence is strictly greater than the configured exact value.

An existing checkpoint always wins. Deleting the adapter's checkpoint reapplies the configured startup mode.

### Delivery and state

Rows are read using ordered keyset pagination and emitted individually. A checkpoint advances only after every row in a
batch has been handed to the collector. A crash between handoff and checkpoint persistence can replay at most one batch,
so delivery at this boundary is at least once.

The default file-backed state is stored below the extension asset directory in
`.streampipes/service/mssql-table-polling-checkpoints`. Each checkpoint JSON file exposes the exact cursor as a string and
a revision; the separate `.schema.json` file preserves the schema captured during configuration. Delete the checkpoint
JSON through the checkpoint store or while the adapter is stopped to reset cursor state safely.

### Polling limits and schema changes

The batch size defaults to 500 and the maximum rows per poll defaults to 10,000. The administrator can set the minimum
allowed interval with `SP_MSSQL_POLLING_MIN_INTERVAL_SECONDS` (default: 1). Startup fails if the configured interval is
below this value.

The output contains every source column, including the sequence, and no synthetic timestamp. Schema changes pause
emission and checkpoint advancement until the original column order and JDBC types return. Sequence regression caused by
truncation or reseeding also pauses emission and requires an explicit state reset.

SQL temporal values are emitted as epoch milliseconds and binary values as Base64 strings, matching the MSSQL connector
conventions. Each poll uses and closes one JDBC connection. Connection login and query execution have finite timeouts.

### SQL Server integration test

`MsSqlTablePollingClientIntegrationTest` exercises the connector against a real SQL Server without starting StreamPipes.
Set `SP_TEST_MSSQL_HOST` to enable it; port, database, username, password, encryption, and certificate trust can be set with
the corresponding `SP_TEST_MSSQL_*` variables. The configured account must be allowed to create and remove a test schema.
