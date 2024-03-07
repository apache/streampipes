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
	config config.StreamPipesClientConnectConfig
}

func NewDataLakeMeasures(clientConfig config.StreamPipesClientConnectConfig) *DataLakeMeasure {
	//NewDataLakeMeasure is used to return an instance of *DataLakeMeasure,

	return &DataLakeMeasure{
		config: clientConfig,
	}
}

func (d *DataLakeMeasure) AllDataLakeMeasure() []data_lake.DataLakeMeasure {
	//Get a list of all measure

	getRequest := streampipes_http.NewGetRequest(&serializer.UnmarshalDataLakeMeasures{}, d.config)
	getRequest.SetUrl([]string{"api", "v4", "datalake", "measurements"})
	unmarshalData := getRequest.ExecuteRequest(nil)
	return unmarshalData.([]data_lake.DataLakeMeasure)
}

func (d *DataLakeMeasure) GetSingleDataLakeMeasure(elementId string) data_lake.DataLakeMeasure {
	//Get a measure

	getRequest := streampipes_http.NewGetRequest(&serializer.UnmarshalDataLakeMeasure{}, d.config)
	getRequest.SetUrl([]string{"api", "v4", "datalake", "measure", elementId})
	unmarshalData := getRequest.ExecuteRequest(nil)
	return unmarshalData.(data_lake.DataLakeMeasure)
}

func (d *DataLakeMeasure) GetSingleDataSeries(measureName string) data_lake.DataSeries {

	//Get data from a single measurement series by a given id
	//Currently not supporting parameter queries

	getRequest := streampipes_http.NewGetRequest(&serializer.UnmarshalDataSeries{}, d.config)
	getRequest.SetUrl([]string{"api", "v4", "datalake", "measurements", measureName})
	unmarshalData := getRequest.ExecuteRequest(nil)
	return unmarshalData.(data_lake.DataSeries)
}

func (d *DataLakeMeasure) ClearDataLakeMeasureData(measureName string) {
	//Remove data from a single measurement series with given id

	deleteRequest := streampipes_http.NewDeleteRequest(d.config)
	deleteRequest.SetUrl([]string{"api", "v4", "datalake", "measurements", measureName})
	deleteRequest.ExecuteRequest(nil)
	log.Printf("Successfully deleted data from a single measurement sequence of %s", measureName)
}

func (d *DataLakeMeasure) DeleteDataLakeMeasure(measureName string) {
	//Drop a single measurement series with given id from Data Lake and remove related event property

	deleteRequest := streampipes_http.NewDeleteRequest(d.config)
	deleteRequest.SetUrl([]string{"api", "v4", "datalake", "measurements", measureName, "drop"})
	deleteRequest.ExecuteRequest(nil)
	log.Printf("Successfully dropped a single measurement series for %s from  DataLake and remove related event property", measureName)
}
