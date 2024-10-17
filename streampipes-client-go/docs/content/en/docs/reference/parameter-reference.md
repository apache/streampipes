---
title: Parameter Reference
description: >
  Implementation of the StreamPipes GO Client.
date: 2024-06-26
---
<!--
  // Licensed to the Apache Software Foundation (ASF) under one or more
  // contributor license agreements.  See the NOTICE file distributed with
  // this work for additional information regarding copyright ownership.
  // The ASF licenses this file to You under the Apache License, Version 2.0
  // (the "License"); you may not use this file except in compliance with
  // the License.  You may obtain a copy of the License at
  //
  //    http://www.apache.org/licenses/LICENSE-2.0
  //
  // Unless required by applicable law or agreed to in writing, software
  // distributed under the License is distributed on an "AS IS" BASIS,
  // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  // See the License for the specific language governing permissions and
  // limitations under the License.
  //
  -->

{{% pageinfo %}}
A short lead description about the reference of the StreamPipes GO Client.
{{% /pageinfo %}}

## StreamPipesClientConfig

`StreamPipesClientConfig` can be configured in a number of ways:

- Approach 1: Directly using struct initialization

```go
clientConfig := config.StreamPipesClientConfig{
    Url: "http://localhost:8030",
    Credential: config.StreamPipesApiKeyCredentials{
        UserName: "<Your-User-Name>",
        ApiKey:   "<Your-API-Key>",
    },
}
```

- Approach 2: Using constructor functions to create the configuration

```go
clientConfig := config.NewStreamPipesClientConnectConfig(
    "http://localhost:8030",
    config.NewStreamPipesApiKeyCredentials("<Your-User-Name>", "<Your-API-Key>")
)
```

- Approach 3: Combining struct initialization with constructor functions

```go
clientConfig := config.StreamPipesClientConfig{
    Url: "http://localhost:8030",
    Credential: config.NewStreamPipesApiKeyCredentials("<Your-User-Name>", "<Your-API-Key>"),
}
```
