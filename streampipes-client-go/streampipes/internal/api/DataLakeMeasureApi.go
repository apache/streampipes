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
	"streampipes-client-go/streampipes/model/DataLake"
)

type DataLakeMeasureApi struct {
	config     config.StreamPipesClientConnectionConfig
	getRequest *StreamPipesHttp.GetRequest
}

func NewDataLakeMeasureApi(clientConfig config.StreamPipesClientConnectionConfig) *DataLakeMeasureApi {

	return &DataLakeMeasureApi{config: clientConfig, getRequest: StreamPipesHttp.NewGetRequest(clientConfig, nil)}
}

func (api *DataLakeMeasureApi) All() []DataLake.DataLakeMeasure {
	Serializer := serializer.NewBaseUnSerializer(serializer.WithUnSerializerDataLakeMeasures(new([]DataLake.DataLakeMeasure)))
	api.getRequest.Serializer = Serializer
	api.ResourcePath(nil)
	api.getRequest.ExecuteGetRequest(api.getRequest.Serializer.UnSerializerDataLakeMeasures)
	return *api.getRequest.Serializer.UnSerializerDataLakeMeasures
}

func (api *DataLakeMeasureApi) Len() int {
	measures := api.All()
	return len(measures[0].EventSchema.EventProperties)
}

func (api *DataLakeMeasureApi) GetSingle(id string) DataLake.DataSeries {
	Serializer := serializer.NewBaseUnSerializer(serializer.WithUnSerializerDataSeries(new(DataLake.DataSeries)))
	api.getRequest.Serializer = Serializer
	api.ResourcePath([]string{id})
	api.getRequest.ExecuteGetRequest(api.getRequest.Serializer.UnSerializerDataLakeSeries)
	return *api.getRequest.Serializer.UnSerializerDataLakeSeries
}

func (api *DataLakeMeasureApi) Create(element DataLake.DataLakeMeasure) error {

	return fmt.Errorf("Not yet implemented")
}

func (api *DataLakeMeasureApi) Delete(elementId string) error {

	return fmt.Errorf("Not yet implemented")
}

func (api *DataLakeMeasureApi) Update(measure DataLake.DataLakeMeasure) error {
	return fmt.Errorf("Not yet implemented") //

}

func (d *DataLakeMeasureApi) ResourcePath(parameter []string) {

	slice := []string{"api", "v4", "datalake", "measurements"}
	slice = append(slice, parameter...)
	d.getRequest.HttpRequest.MakeUrl(slice)
}
