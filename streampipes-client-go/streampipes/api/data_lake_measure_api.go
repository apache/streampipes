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
	"streampipes-client-go/streampipes/internal/serializer"
	"streampipes-client-go/streampipes/internal/streampipes_http"
	"streampipes-client-go/streampipes/model/data_lake"
)

/*
DataLakeMeasureApi connects to the DataLakeMeasure endpoint of Streampipes.
DataLakeMeasureApi supports GET, POST, Delete, and PUT methods for obtaining, deleting, submitting, and updating resources.
The specific interaction behavior is provided by the method bound to the DataLakeMeasureApi struct.
Currently, only some GET and Delete methods have been implemented.
*/
type DataLakeMeasureApi struct {
	config      config.StreamPipesClientConnectionConfig
	httpRequest streampipes_http.HttpRequest
}

func NewDataLakeMeasureApi(clientConfig config.StreamPipesClientConnectionConfig) *DataLakeMeasureApi {
	//NewDataLakeMeasureApi is used to return an instance of *DataLakeMeasureApi,

	return &DataLakeMeasureApi{
		config:      clientConfig,
		httpRequest: nil,
	}
}

func (d *DataLakeMeasureApi) All() []data_lake.DataLakeMeasure {
	//Get a list of all measurement series
	//Deserializes the data into the corresponding DataLakeMeasure data model.

	UnSerializer := serializer.NewBaseUnSerializer(serializer.WithUnSerializerDataLakeMeasures())
	d.httpRequest = &streampipes_http.GetRequest{
		HttpRequest:  streampipes_http.NewHttpRequest(d.config),
		UnSerializer: UnSerializer,
	}
	d.ResourcePath(nil)
	interfaces := d.httpRequest.ExecuteRequest(nil)
	UnBaseSerializer := interfaces.(*serializer.UnBaseSerializer)
	return *UnBaseSerializer.UnSerializerDataLakeMeasures
}

func (d *DataLakeMeasureApi) GetSingle(id string) data_lake.DataSeries {

	//Get data from a single measurement series by a given id

	UnSerializer := serializer.NewBaseUnSerializer(serializer.WithUnSerializerDataSeries())
	d.httpRequest = &streampipes_http.GetRequest{
		HttpRequest:  streampipes_http.NewHttpRequest(d.config),
		UnSerializer: UnSerializer,
	}
	d.ResourcePath([]string{id})
	interfaces := d.httpRequest.ExecuteRequest(nil)
	UnBaseSerializer := interfaces.(*serializer.UnBaseSerializer)
	return *UnBaseSerializer.UnSerializerDataLakeSeries
}

func (d *DataLakeMeasureApi) DeleteMeasurementInternalData(elementId string) string {
	//Remove data from a single measurement series with given id

	d.httpRequest = &streampipes_http.DeleteRequest{
		HttpRequest:  streampipes_http.NewHttpRequest(d.config),
		UnSerializer: nil,
	}
	d.ResourcePath([]string{elementId})
	interfaces := d.httpRequest.ExecuteRequest(nil)
	return interfaces.(string)
}

func (d *DataLakeMeasureApi) DeleteMeasurementSeries(elementId string) string {
	//Drop a single measurement series with given id from Data Lake and remove related event property

	d.httpRequest = &streampipes_http.DeleteRequest{
		HttpRequest:  streampipes_http.NewHttpRequest(d.config),
		UnSerializer: nil,
	}
	d.ResourcePath([]string{elementId, "drop"})
	interfaces := d.httpRequest.ExecuteRequest(nil)
	return interfaces.(string)
}

func (d *DataLakeMeasureApi) Create(element data_lake.DataLakeMeasure) error {

	return fmt.Errorf("Not yet implemented")
}

func (d *DataLakeMeasureApi) Update(measure data_lake.DataLakeMeasure) error {
	return fmt.Errorf("Not yet implemented")

}

func (d *DataLakeMeasureApi) ResourcePath(parameter []string) {

	//ResourcePath is the path to obtain resources for the StreamPipes API endpoint
	//Parameter is a path parameter, for example: add  /measurementId  after baseResourcePath (API/v4/datalake/measures)
	//so it is API/v4/datalake/measures/measurmentId

	baseResourcePath := []string{"api", "v4", "datalake", "measurements"}
	baseResourcePath = append(baseResourcePath, parameter...)
	d.httpRequest.MakeUrl(baseResourcePath)
}
