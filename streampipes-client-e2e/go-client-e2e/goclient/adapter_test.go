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

package goclient

import (
	"fmt"
	"github.com/apache/streampipes/streampipes-client-go/streampipes"
	"github.com/apache/streampipes/streampipes-client-go/streampipes/config"
	"github.com/apache/streampipes/streampipes-client-go/streampipes/model"
	"github.com/apache/streampipes/streampipes-client-go/streampipes/model/adapter"
	"os"
	"testing"
)

func TestAdapterCreate(t *testing.T) {
	apiKey := os.Getenv("APIKEY")
	userName := os.Getenv("API_KEY_USER_NAME")
	clientConfig := config.StreamPipesClientConfig{
		Url: "",
		Credential: config.StreamPipesApiKeyCredentials{
			UserName: apiKey,
			ApiKey:   userName,
		},
	}
	streamPipesClient, err := streampipes.NewStreamPipesClient(clientConfig)
	if err != nil {
		os.Exit(1)
	}

	// adapterData数据怎么确定？手动填还是get另外一个adapter的数据，再转成adapterData？
	adapterData := adapter.AdapterDescription{
		ElementID:                        "",
		Rev:                              "",
		DOM:                              "",
		ConnectedTo:                      nil,
		Name:                             "",
		Description:                      "",
		AppID:                            "",
		IncludesAssets:                   false,
		IncludesLocales:                  false,
		IncludedAssets:                   nil,
		IncludedLocales:                  nil,
		InternallyManaged:                false,
		Version:                          0,
		DataStream:                       model.SpDataStream{},
		Running:                          false,
		EventGrounding:                   model.EventGrounding{},
		Icon:                             "",
		Config:                           nil,
		Rules:                            nil,
		Category:                         nil,
		CreatedAt:                        0,
		SelectedEndpointURL:              "",
		DeploymentConfiguration:          model.ExtensionDeploymentConfiguration{},
		CorrespondingDataStreamElementID: "",
		EventSchema:                      model.EventSchema{},
		ValueRules:                       nil,
		StreamRules:                      nil,
		SchemaRules:                      nil,
	}
	err = streamPipesClient.Adapter().CreateAdapter(adapterData)
	if err != nil {
		os.Exit(1)
	}
	fmt.Println("create adapter success!")
	err = streamPipesClient.Adapter().StartSingleAdapter("") // 看看上面CREATE后有没有返回adapterId
	if err != nil {
		os.Exit(1)
	}
	fmt.Println("start adapter success!")
}
