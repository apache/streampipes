---
title: Manage Pipeline
description: >
  Manage Pipeline.
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

#  Manage StreamPipes Pipeline
---
## Initialize Go-Client
```go
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

}
```

### Create a StreamPipes Pipeline

```go
      // Populating go-model as a data source is not supported at this time,This is because the inheritance of the model has not been resolved for the time being.
      pipelineData = `json`
      pipeline = []byte(pipelineData)
	  responseMsg,err := streamPipesClient.Pipeline().CreatePipeline(pipelineData)
```

### Get a StreamPipes Pipeline

```go
	streampipesPipeline, err := streamPipesClient.Pipeline().GetSinglePipelineStatus("pipelineId")
```

### Start a StreamPipes Pipeline

```go
	operationStatus, err := streamPipesClient.Pipeline().StartSinglePipeline("pipelineId")
```

### Stop a StreamPipes Pipeline

```go
	operationStatus, err := streamPipesClient.Pipeline().StopSinglePipeline("pipelineId")
```

### ...
