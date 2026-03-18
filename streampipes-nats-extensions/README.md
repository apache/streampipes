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

# StreamPipes NATS Extensions

This module receives core-to-extension commands over NATS request/reply.

## Configuration

- `SP_EXTENSION_TRANSPORT_MODE` (extension side): `http`, `nats`, or `dual`
- `SP_CORE_EXTENSION_TRANSPORT_MODE` (core side): `http`, `nats`, or `auto`
- `SP_EXTENSION_REQUEST_TOPIC_PREFIX` (both sides): default `sp.extensions.request`
- `SP_NATS_HOST`: default `nats`
- `SP_NATS_PORT`: default `4222`
- `SP_NATS_TOKEN`: optional NATS token for internal core<->extension communication

## Subject Structure

Base subject format:

```text
<topic-prefix>.<service-id>.<operation-segments...>
```

The receiver subscribes to:

```text
<topic-prefix>.<service-id>.>
```

Example base:

```text
sp.extensions.request.a1B2c3
```

## Segment Encoding

Topic segments are encoded with `~XX` (uppercase hex, UTF-8 bytes). Safe bytes are `A-Z`, `a-z`, `0-9`, `-`, `_`.

The `service-id` is not encoded. In practice it is auto-generated as 6-char alphanumeric (`AUTO_GENERATED_SERVICE_ID`).

Examples:

- `org.apache.streampipes.demo` -> `org~2Eapache~2Estreampipes~2Edemo`
- `a/b` -> `a~2Fb`
- `~` -> `~7E`

## Topic Catalog (with examples)

All examples use this service id:

```text
a1B2c3
```

| Operation ID (`request.operation`) | Topic Template (after service id) | Example Subject |
|---|---|---|
| `CONTAINER_PROVIDED_OPTIONS` | `container-provided-options.<PROVIDER>.<APP_ID>` | `sp.extensions.request.a1B2c3.container-provided-options.DATA_PROCESSOR.org~2Eapache~2Estreampipes~2Eproc` |
| `MIGRATION` | `migration.<TYPE>` | `sp.extensions.request.a1B2c3.migration.sink` |
| `DESCRIPTION_UPDATE` | `description-update.<PROVIDER>.<APP_ID>` | `sp.extensions.request.a1B2c3.description-update.DATA_SINK.org~2Eapache~2Estreampipes~2Esink` |
| `EXTENSION_DESCRIPTION` | `extension-description.<PROVIDER>.<APP_ID>` | `sp.extensions.request.a1B2c3.extension-description.DATA_STREAM.org~2Eapache~2Estreampipes~2Estream` |
| `FUNCTION_STOP` | `function-stop` | `sp.extensions.request.a1B2c3.function-stop` |
| `ADAPTER_STATE_CHANGE` | `adapter-state-change.<COMMAND>` | `sp.extensions.request.a1B2c3.adapter-state-change.start` |
| `RUNTIME_OPTIONS` | `adapter-runtime-options.<APP_ID>` | `sp.extensions.request.a1B2c3.adapter-runtime-options.org~2Eapache~2Estreampipes~2Eadapter` |
| `SAMPLE_DATA` | `adapter-sample-data` | `sp.extensions.request.a1B2c3.adapter-sample-data` |
| `EXTENSION_INSTANCE_HEALTH` | `extension-instance-health` | `sp.extensions.request.a1B2c3.extension-instance-health` |
| `SERVICE_HEALTH` | `service-health` | `sp.extensions.request.a1B2c3.service-health` |
| `SERVICE_LOAD` | `monitoring.service-load` | `sp.extensions.request.a1B2c3.monitoring.service-load` |
| `PIPELINE_ELEMENT_INVOCATION` | `pipeline-invocation.<PROVIDER>.<APP_ID>` | `sp.extensions.request.a1B2c3.pipeline-invocation.DATA_PROCESSOR.org~2Eapache~2Estreampipes~2Eproc` |
| `PIPELINE_ELEMENT_DETACH` | `pipeline-detach.<PROVIDER>.<APP_ID>.<INSTANCE_ID>` | `sp.extensions.request.a1B2c3.pipeline-detach.DATA_PROCESSOR.org~2Eapache~2Estreampipes~2Eproc.instance-42` |
| `PIPELINE_ELEMENT_ASSETS` | `pipeline-element-assets.<PROVIDER>.<APP_ID>` | `sp.extensions.request.a1B2c3.pipeline-element-assets.DATA_PROCESSOR.org~2Eapache~2Estreampipes~2Eproc` |
| `ADAPTER_ASSETS` | `adapter-assets.<APP_ID>` | `sp.extensions.request.a1B2c3.adapter-assets.org~2Eapache~2Estreampipes~2Eadapter` |
| `ADAPTER_ICON_ASSET` | `adapter-icon-asset.<APP_ID>` | `sp.extensions.request.a1B2c3.adapter-icon-asset.org~2Eapache~2Estreampipes~2Eadapter` |
| `ADAPTER_DOCUMENTATION_ASSET` | `adapter-documentation-asset.<APP_ID>` | `sp.extensions.request.a1B2c3.adapter-documentation-asset.org~2Eapache~2Estreampipes~2Eadapter` |
| `OUTPUT_SCHEMA` | `output-schema.<PROVIDER>.<APP_ID>` | `sp.extensions.request.a1B2c3.output-schema.DATA_PROCESSOR.org~2Eapache~2Estreampipes~2Eproc` |

`<PROVIDER>` values currently used by the core topic builder:

- `DATA_PROCESSOR`
- `DATA_SINK`
- `DATA_STREAM`
- `ADAPTER`

## Request/Response Envelope

Request payload (JSON):

```json
{
  "requestId": "1c89e2dc-1f49-4d08-a662-9ced4de2168d",
  "operation": "OUTPUT_SCHEMA",
  "payload": "{\"appId\":\"org.apache.streampipes.proc\"}",
  "authToken": null
}
```

Response payload (JSON):

```json
{
  "requestId": "1c89e2dc-1f49-4d08-a662-9ced4de2168d",
  "statusCode": 200,
  "payload": "{\"success\":true}",
  "payloadBytes": null,
  "error": null
}
```
