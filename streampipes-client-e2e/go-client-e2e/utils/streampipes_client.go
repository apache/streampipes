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

package utils

import (
	"github.com/apache/streampipes/streampipes-client-go/streampipes"
	"github.com/apache/streampipes/streampipes-client-go/streampipes/config"
	"os"
)

func CreateStreamPipesClient() (*streampipes.StreamPipesClient, error) {
	length := len(os.Args)
	clientConfig := config.StreamPipesClientConfig{
		Url: "http://" + os.Args[length-4] + ":" + os.Args[length-3],
		Credential: config.StreamPipesApiKeyCredentials{
			UserName: os.Args[length-1],
			ApiKey:   os.Args[length-2],
		},
	}
	return streampipes.NewStreamPipesClient(clientConfig)
}
