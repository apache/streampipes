//
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

package main

import (
	"fmt"
	"streampipes-client-go/streampipes/internal"
	"streampipes-client-go/streampipes/internal/config"
	"streampipes-client-go/streampipes/internal/credential"
)

func main() {
	Config := config.StreamPipesClientConnectionConfig{
		Credential: credential.StreamPipesApiKeyCredentials{
			Username: "admin@streampipes.apache.org",
			ApiKey:   "LNrsh8YrgEyQTzSKSGmaAXb1",
		},
		StreamPipesPort: "8030",
		StreamPipesHost: "localhost",
		HttpsDisabled:   true,
	}
	StreamPipesClient, err := internal.NewStreamPipesClient(Config)
	if err != nil {
		fmt.Println(err)
	}
	measure := StreamPipesClient.DataLakeMeasureApi().All()

	for k := range measure {
		fmt.Println(measure[k])
		fmt.Println(measure[k].GetEventSchema())
	}

}
