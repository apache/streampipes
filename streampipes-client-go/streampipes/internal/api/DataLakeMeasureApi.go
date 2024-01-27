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

package api

import (
	"fmt"
	"streampipes-client-go/streampipes/internal/StreamPipesHttp"
	"streampipes-client-go/streampipes/internal/config"
	"streampipes-client-go/streampipes/internal/serializer"
	"streampipes-client-go/streampipes/model/resource"
)

type DataLakeMeasureApi struct {
	config     config.StreamPipesClientConnectionConfig
	getRequest *StreamPipesHttp.GetRequest
	serializer *serializer.UnBaseSerializer
}

func NewDataLakeMeasureApi(clientConfig config.StreamPipesClientConnectionConfig) *DataLakeMeasureApi {
	Serializer := &serializer.UnBaseSerializer{
		UnSerializerDataLakeMeasure: nil,
	}
	return &DataLakeMeasureApi{config: clientConfig, getRequest: StreamPipesHttp.NewGetRequest(clientConfig, Serializer), serializer: Serializer}
}

func (api *DataLakeMeasureApi) All() []resource.DataLakeMeasure {
	api.ResourcePath()
	api.getRequest.ExecuteGetRequest(api.serializer.UnSerializerDataLakeMeasure)
	return *api.serializer.UnSerializerDataLakeMeasure
}

//func (api *DataLakeMeasureApi) Len() int {
//	k := api.Get()
//	return len(k)
//}

//func (api *DataLakeMeasureApi) Get(id string) ([]resource.DataLakeMeasure, error) {
//
//}

func (api *DataLakeMeasureApi) Create(element resource.DataLakeMeasure) error {

	return fmt.Errorf("Not yet implemented")
}

func (api *DataLakeMeasureApi) Delete(elementId string) error {

	return fmt.Errorf("Not yet implemented")
}

func (api *DataLakeMeasureApi) Update(measure resource.DataLakeMeasure) error {
	return fmt.Errorf("Not yet implemented") //

}

func (d *DataLakeMeasureApi) ResourcePath() {

	slice := []string{"api", "v4", "datalake", "measurements"}
	d.getRequest.HttpRequest.MakeUrl(slice)
}
