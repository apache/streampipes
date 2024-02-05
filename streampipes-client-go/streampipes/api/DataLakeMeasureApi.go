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
	"streampipes-client-go/streampipes/config"
	"streampipes-client-go/streampipes/internal/StreamPipesHttp"
	"streampipes-client-go/streampipes/internal/serializer"
	"streampipes-client-go/streampipes/model/DataLake"
)

type DataLakeMeasureApi struct {
	config      config.StreamPipesClientConnectionConfig
	httpRequest StreamPipesHttp.HttpRequest
}

func NewDataLakeMeasureApi(clientConfig config.StreamPipesClientConnectionConfig) *DataLakeMeasureApi {

	return &DataLakeMeasureApi{
		config:      clientConfig,
		httpRequest: nil,
	}
}

func (api *DataLakeMeasureApi) All() []DataLake.DataLakeMeasure {
	//Get a list of all measurement series
	UnSerializer := serializer.NewBaseUnSerializer(serializer.WithUnSerializerDataLakeMeasures())
	api.httpRequest = &StreamPipesHttp.GetRequest{
		HttpRequest:  StreamPipesHttp.NewHttpRequest(api.config),
		UnSerializer: UnSerializer,
	}
	api.ResourcePath(nil)
	interfaces := api.httpRequest.ExecuteRequest(nil)
	UnBaseSerializer := interfaces.(*serializer.UnBaseSerializer)
	return *UnBaseSerializer.UnSerializerDataLakeMeasures
}

func (api *DataLakeMeasureApi) GetSingle(id string) DataLake.DataSeries {
	//Get data from a single measurement series by a given id
	UnSerializer := serializer.NewBaseUnSerializer(serializer.WithUnSerializerDataSeries())
	api.httpRequest = &StreamPipesHttp.GetRequest{
		HttpRequest:  StreamPipesHttp.NewHttpRequest(api.config),
		UnSerializer: UnSerializer,
	}
	api.ResourcePath([]string{id})
	interfaces := api.httpRequest.ExecuteRequest(nil)
	UnBaseSerializer := interfaces.(*serializer.UnBaseSerializer)
	return *UnBaseSerializer.UnSerializerDataLakeSeries
}

func (api *DataLakeMeasureApi) DeleteMeasurementInternalData(elementId string) string {
	//Remove data from a single measurement series with given id
	api.httpRequest = &StreamPipesHttp.DeleteRequest{
		HttpRequest:  StreamPipesHttp.NewHttpRequest(api.config),
		UnSerializer: nil,
	}
	api.ResourcePath([]string{elementId})
	interfaces := api.httpRequest.ExecuteRequest(nil)
	return interfaces.(string)
}

func (api *DataLakeMeasureApi) DeleteMeasurementSeries(elementId string) string {
	//Drop a single measurement series with given id from Data Lake and remove related event property
	api.httpRequest = &StreamPipesHttp.DeleteRequest{
		HttpRequest:  StreamPipesHttp.NewHttpRequest(api.config),
		UnSerializer: nil,
	}
	api.ResourcePath([]string{elementId, "drop"})
	interfaces := api.httpRequest.ExecuteRequest(nil)
	return interfaces.(string)
}

func (api *DataLakeMeasureApi) Create(element DataLake.DataLakeMeasure) error {

	return fmt.Errorf("Not yet implemented")
}

func (api *DataLakeMeasureApi) Update(measure DataLake.DataLakeMeasure) error {
	return fmt.Errorf("Not yet implemented") //

}

func (d *DataLakeMeasureApi) ResourcePath(parameter []string) {

	slice := []string{"api", "v4", "datalake", "measurements"}
	slice = append(slice, parameter...)
	d.httpRequest.MakeUrl(slice)
}
