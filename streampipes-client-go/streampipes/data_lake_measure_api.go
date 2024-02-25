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

package streampipes

import (
	"github.com/apache/streampipes/streampipes-client-go/streampipes/config"
	"github.com/apache/streampipes/streampipes-client-go/streampipes/internal/serializer"
	"github.com/apache/streampipes/streampipes-client-go/streampipes/internal/streampipes_http"
	"github.com/apache/streampipes/streampipes-client-go/streampipes/model/data_lake"
	"log"
)

/*
DataLakeMeasure connects to the DataLakeMeasure endpoint of Streampipes.
DataLakeMeasure supports GET and DELETE to delete or obtain resources
The specific interaction behavior is provided by the method bound to the DataLakeMeasure struct.
*/

type DataLakeMeasure struct {
	config      config.StreamPipesClientConnectionConfig
	httpRequest streampipes_http.HttpRequest
}

func NewDataLakeMeasures(clientConfig config.StreamPipesClientConnectionConfig) *DataLakeMeasure {
	//NewDataLakeMeasure is used to return an instance of *DataLakeMeasure,

	return &DataLakeMeasure{
		config:      clientConfig,
		httpRequest: nil,
	}
}

func (d *DataLakeMeasure) AllDataLakeMeasure() []data_lake.DataLakeMeasure {
	//Get a list of all measure

	d.httpRequest = &streampipes_http.GetRequest{
		HttpRequest:  streampipes_http.NewHttpRequest(d.config),
		Deserializer: &serializer.UnmarshalDataLakeMeasure{},
	}
	d.resourcePath([]string{"measurements"})
	UnmarshalData := d.httpRequest.ExecuteRequest(nil)
	return UnmarshalData.([]data_lake.DataLakeMeasure)
}

func (d *DataLakeMeasure) GetSingleDataLakeMeasure(elementId string) data_lake.DataLakeMeasure {
	//Get a measure

	d.httpRequest = &streampipes_http.GetRequest{
		HttpRequest:  streampipes_http.NewHttpRequest(d.config),
		Deserializer: &serializer.UnmarshalDataLakeMeasure{},
	}
	d.resourcePath([]string{"measure", elementId})
	UnmarshalData := d.httpRequest.ExecuteRequest(nil)
	return UnmarshalData.(data_lake.DataLakeMeasure)
}

func (d *DataLakeMeasure) GetSingleDataSeries(measureName string) data_lake.DataSeries {

	//Get data from a single measurement series by a given id
	//Currently not supporting parameter queries

	d.httpRequest = &streampipes_http.GetRequest{
		HttpRequest:  streampipes_http.NewHttpRequest(d.config),
		Deserializer: &serializer.UnmarshalDataSeries{},
	}
	d.resourcePath([]string{"measurements", measureName})
	UnmarshalData := d.httpRequest.ExecuteRequest(nil)
	return UnmarshalData.(data_lake.DataSeries)
}

func (d *DataLakeMeasure) ClearDataLakeMeasureData(measureName string) {
	//Remove data from a single measurement series with given id

	d.httpRequest = &streampipes_http.DeleteRequest{
		HttpRequest: streampipes_http.NewHttpRequest(d.config),
	}
	d.resourcePath([]string{"measurements", measureName})
	d.httpRequest.ExecuteRequest(nil)
	log.Printf("Successfully deleted data from a single measurement sequence of %s", measureName)
}

func (d *DataLakeMeasure) DeleteDataLakeMeasure(measureName string) {
	//Drop a single measurement series with given id from Data Lake and remove related event property

	d.httpRequest = &streampipes_http.DeleteRequest{
		HttpRequest: streampipes_http.NewHttpRequest(d.config),
	}
	d.resourcePath([]string{"measurements", measureName, "drop"})
	d.httpRequest.ExecuteRequest(nil)
	log.Printf("Successfully dropped a single measurement series for %s from  DataLake and remove related event property", measureName)
}

func (d *DataLakeMeasure) resourcePath(parameter []string) {

	//ResourcePath is the path to obtain resources for the StreamPipes API endpoint
	//Parameter is a path parameter, for example: add  /measurementId  after baseResourcePath (API/v4/datalake/measures)
	//so it is API/v4/datalake/measures/measurmentId

	baseResourcePath := []string{"api", "v4", "datalake"}
	baseResourcePath = append(baseResourcePath, parameter...)
	d.httpRequest.SetUrl(baseResourcePath)
}
