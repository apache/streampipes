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

## PostgreSQL

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

Stores events in a Postgres database.

***

## Required input

This sink does not have any requirements and works with any incoming event type.

***

## Configuration

### Hostname

The hostname of the PostgreSQL instance.

### Port

The port of the PostgreSQL instance (default 5432).

### Database Name

The name of the database where events will be stored

### Table Name

The name of the table where events will be stored (will be created if it does not exist)

### Allow New Table Creation

When enabled, the sink creates the table entered above if it does not exist yet.
When disabled, the pipeline does not start and asks you to create the table first.
Use this if the tables in your database have to be created by you.

### Username

The username for the PostgreSQL Server.

### Password

The password for the PostgreSQL Server.

### Batch Size

The number of events collected before they are written together to the database. Use the value 1 to
write each event on its own. Higher values are faster at high data rates. Buffered events are flushed
when the pipeline stops.

## Output

(not applicable for data sinks)
