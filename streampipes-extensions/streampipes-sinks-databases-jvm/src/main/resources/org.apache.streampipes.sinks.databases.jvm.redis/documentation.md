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

## Redis

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Description

Stores events in a Redis key-value store.

***

## Required input

This sink does not have any requirements and works with any incoming event type.

***

## Configuration

Describe the configuration parameters here

### Hostname
The hostname of the Redis instance

### Port
The port of the Redis instance (default 6379)

### Key Field
Runtime field to be used as the key when storing the event. If auto-increment is enabled, this setting will be ignored.

### Auto Increment
Enabling this will generate a sequential numeric key for every record inserted. (note: enabling this will ignore Key Field)

### Expiration Time (Optional)
The expiration time for a persisted event.

### Password (Optional)
The password for the Redis instance.

### Connection Name (Optional)
A connection name to assign for the current connection.

### DB Index (Optional)
Zero-based numeric index for Redis database.

### Max Active (Redis Pool) (Optional)
The maximum number of connections that can be allocated from the pool.

### Max Idle (Redis Pool) (Optional)
The maximum number of connections that can remain idle in the pool.

### Max Wait (Redis Pool) (Optional)
The maximum number of milliseconds that the caller needs to wait when no connection is available.

### Max Timeout (Redis Pool) (Optional)
The maximum time for connection timeout and read/write timeout.

## Output

(not applicable for data sinks)