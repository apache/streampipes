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

# OPC UA Events

This adapter subscribes to OPC UA server events and emits read-only OPC UA event payloads.

## Current scope

- Reuses the same OPC UA connection, security, and authentication settings as the OPC UA value adapter
- Subscribes either server-wide or to a selected notifier node
- Uses a selected OPC UA event type as a server-side `OfType(...)` filter
- Lets users add optional event fields derived from the selected event type
- Reads events only; acknowledge, confirm, and shelving actions are not part of this adapter yet

## Configuration

### 1. Connection

The connection settings are identical to the regular OPC UA adapter:

- server URL or host/port
- security mode and security policy
- anonymous, username/password, or X.509 authentication

### 2. Event source

The adapter supports two source scopes:

- `Whole server`
- `Specific area or machine`

If a specific area or machine is selected, the notifier tree is loaded lazily from the OPC UA address space. Only event-capable object nodes are selectable.

### 3. Event type

The event type is selected from the OPC UA event type tree.

This selection has two effects:

- it defines the server-side `OfType(...)` filter for the event subscription
- it defines which additional event fields can be selected

The tree is ordered alphabetically by browse name.

### 4. Additional fields

The adapter always includes a compatibility-safe base field set internally:

- `sourceName`
- `severity`
- `sourceNode`
- `message`
- `time`
- `eventId`
- `eventType`

In addition, users can select optional fields in the `Additional fields` section.

These fields are derived from the actual instance declarations of the selected event type and its supertypes. For example, if a limit-alarm subtype exposes limit-related declarations, those declarations appear in the selectable field list.

Important:

- after changing the selected event type, reload the `Additional fields` list
- additional fields are optional because some OPC UA servers stop delivering events if too many event fields are requested in the subscription

### 5. Event filters

The adapter currently supports these user-facing filters:

- all matching events
- `Source name contains`
- minimum severity

The source-name filter is applied to the OPC UA `SourceName` field.

## Output model

The emitted event payload is a flat `Map<String, Object>`.

Behavior:

- base event fields are always present
- selected additional fields are included in the output schema and requested from the server
- values are normalized to script-friendly representations

Examples of normalization:

- `ByteString` -> Base64 string
- `DateTime` -> Java timestamp in milliseconds
- `NodeId` / `ExpandedNodeId` / `QualifiedName` -> parseable string
- unsigned OPC UA numbers -> Java numeric values
- arrays and matrices -> lists / maps

## Field compatibility

OPC UA event field selection is server-dependent.

A field may be:

- returned with a value
- returned as `null`
- accepted by the server but never populated
- incompatible with the server's event implementation, which can stop event delivery for that subscription

For that reason, the adapter uses a conservative default base field set and lets users add subtype-specific fields manually.

Recommended workflow:

1. select the event type
2. verify that preview works with the base field set
3. reload and add optional fields incrementally
4. keep the largest field set that still delivers events reliably

## Preview behavior

The preview step waits for a live event notification.

Important implications:

- a successful connection does not guarantee that preview data appears immediately
- if the server does not emit a new matching event during the preview window, the preview can time out even though the configuration is valid
- retained/current alarms are not guaranteed to be replayed automatically by every server during preview

## Limitations

- read-only only
- no acknowledge / confirm / shelving operations
- no historical event retrieval
- no automatic negotiation of incompatible optional fields
- field availability depends on what the server exposes in its event type declarations
