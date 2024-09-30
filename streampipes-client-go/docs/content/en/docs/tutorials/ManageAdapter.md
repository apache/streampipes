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

###  Manage StreamPipes Adapter
---
## Initialize Go-Client
```bash
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

### Create a StreamPipes Adapter

```bash
	  adapterData := adapter.AdapterDescription{} // Populate the adapter data
	  err := streamPipesClient.Adapter().CreateAdapter(adapterData)
```

### Get a StreamPipes Adapter

```bash
	streampipesAdapter, err := streamPipesClient.Adapter().GetSingleAdapter("adapterId")
```

### Start a StreamPipes Adapter

```bash
	err := streamPipesClient.Adapter().StartSingleAdapter("adapterId")
```

### Stop a StreamPipes Adapter

```bash
	err := streamPipesClient.Adapter().StopSingleAdapter("adapterId")
```

### ... 