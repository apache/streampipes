---
title: Parameter Reference
description: >
  Implementation of the StreamPipes GO Client.
date: 2024-06-26
---

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
