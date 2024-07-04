---
title: Quickstart
description: >
  Quickstart to use StreamPipes Go.
---

{{% pageinfo %}}
As a quick example, we demonstrate how to set up and configure a StreamPipes Go Client. In addition, we will get the available Data Lake Measures out of StreamPipes.
{{% /pageinfo %}}

## 1. Prerequisites

Before you start, make sure you have the [First Steps](./first-steps.md) prerequisites below:

- Go environment installed on your machine.
- Access to a running Apache StreamPipes instance.

## 2. Configuration

Client requires to configure the connection parameters to connect to your StreamPipes instance.

It can actually be configured in three ways, but let's choose a straightforward way to start the tutorial.

```go
clientConfig := config.StreamPipesClientConfig{
    Url: "http://localhost:8030",
    Credential: config.StreamPipesApiKeyCredentials{
        UserName: "<Your-User-Name>",
        ApiKey:   "<Your-API-Key>",
    },
}
```

## 3. Initialize Client

Use the configuration to initialize the client.

```go
streamPipesClient, err := streampipes.NewStreamPipesClient(clientConfig)
if err != nil {
    log.Fatal(err)  // Ensure that logging is correctly set up to capture errors
}
```

## 4. Get Data Lake Measures

Request and print the data from a specific data measure.

```go
dataSeries, err := streamPipesClient.DataLakeMeasures().GetSingleDataSeries("measureName")
if err != nil {
    log.Fatal(err)
}
dataSeries.Print()
```

Upon successful execution, the client will print data resembling the following format:

```shell
There are 2 pieces of DataSerie in the DataSeries:
DataSeries 1:
time                       msg               test
2024-02-23T13:37:09.052Z   go-client_test   2f4556
2024-02-23T13:37:26.044Z   go-client_test   2f4556
2024-02-23T13:37:29.007Z   go-client_test   2f4556

DataSeries 2:
time                       msg               test
2024-02-23T13:38:06.052Z   go-client_test   2f4556
2024-02-23T13:38:35.044Z   go-client_test   2f4556
2024-02-23T13:38:38.007Z   go-client_test   2f4556
```

## Complete Quickstart Code

Here's the full code needed to run the quickstart example:

```go
package main

import (
	"log"

	"github.com/apache/streampipes/streampipes-client-go/streampipes"
	"github.com/apache/streampipes/streampipes-client-go/streampipes/config"
)

func main() {
	clientConfig := config.StreamPipesClientConfig{
		Url: "http://localhost:8030",
		Credential: config.StreamPipesApiKeyCredentials{
			UserName: "<Your-User-Name>",
			ApiKey:   "<Your-API-Key>",
		},
	}

	streamPipesClient, err := streampipes.NewStreamPipesClient(clientConfig)
	if err != nil {
		log.Fatal(err)
	}

	dataSeries, err := streamPipesClient.DataLakeMeasures().GetSingleDataSeries("measureName")
	if err != nil {
		log.Fatal(err)
	}
	dataSeries.Print()
}
```
