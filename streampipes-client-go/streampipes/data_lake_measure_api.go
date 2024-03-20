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
	"github.com/apache/streampipes/streampipes-client-go/streampipes/internal/util"
	"github.com/apache/streampipes/streampipes-client-go/streampipes/model/data_lake"
	"io"
	"log"
	"net/http"
)

// DataLakeMeasure connects to the DataLakeMeasure endpoint of streamPipes.
// DataLakeMeasure supports GET and DELETE to delete or obtain resources
// The specific interaction behavior is provided by the method bound to the DataLakeMeasure struct.
type DataLakeMeasure struct {
	endpoint
}

func NewDataLakeMeasures(clientConfig config.StreamPipesClientConfig) *DataLakeMeasure {
	// NewDataLakeMeasure is used to return an instance of *DataLakeMeasure,

	return &DataLakeMeasure{
		endpoint{config: clientConfig},
	}
}

func (d *DataLakeMeasure) AllDataLakeMeasure() ([]data_lake.DataLakeMeasure, error) {
	// Get a list of all measure

	endPointUrl := util.NewStreamPipesApiPath(d.config.Url, "streampipes-backend/api/v4/datalake/measurements", nil)
	log.Printf("Get data from: %s", endPointUrl)
	response, err := d.executeRequest("GET", endPointUrl)
	if err != nil {
		return nil, err
	}

	if response.StatusCode != http.StatusOK {
		err = d.handleStatusCode(response)
		if err != nil {
			return nil, err
		}
	}

	body, err := io.ReadAll(response.Body)
	if err != nil {
		return nil, err
	}

	unmarshalData, err := serializer.NewUnmarshalDataLakeMeasures().GetUnmarshal(body)
	if err != nil {
		return nil, err
	}
	dataLakeMeasures := unmarshalData.([]data_lake.DataLakeMeasure)

	return dataLakeMeasures, nil
}

func (d *DataLakeMeasure) GetSingleDataLakeMeasure(elementId string) (data_lake.DataLakeMeasure, error) {
	// Get a measure

	endPointUrl := util.NewStreamPipesApiPath(d.config.Url, "streampipes-backend/api/v4/datalake/measure", []string{elementId})
	log.Printf("Get data from: %s", endPointUrl)
	response, err := d.executeRequest("GET", endPointUrl)
	if err != nil {
		return data_lake.DataLakeMeasure{}, err
	}

	if response.StatusCode != http.StatusOK {
		err = d.handleStatusCode(response)
		if err != nil {
			return data_lake.DataLakeMeasure{}, err
		}
	}

	body, err := io.ReadAll(response.Body)
	if err != nil {
		return data_lake.DataLakeMeasure{}, err
	}

	unmarshalData, err := serializer.NewUnmarshalDataLakeMeasure().GetUnmarshal(body)
	if err != nil {
		return data_lake.DataLakeMeasure{}, err
	}
	dataLakeMeasure := unmarshalData.(data_lake.DataLakeMeasure)

	return dataLakeMeasure, nil
}

func (d *DataLakeMeasure) GetSingleDataSeries(measureName string) (*data_lake.DataSeries, error) {

	// Get data from a single measurement series by a given id
	// Currently not supporting parameter queries

	endPointUrl := util.NewStreamPipesApiPath(d.config.Url, "streampipes-backend/api/v4/datalake/measurements", []string{measureName})
	log.Printf("Get data from: %s", endPointUrl)
	response, err := d.executeRequest("GET", endPointUrl)
	if err != nil {
		return nil, err
	}

	if response.StatusCode != http.StatusOK {
		err = d.handleStatusCode(response)
		if err != nil {
			return nil, err
		}
	}

	body, err := io.ReadAll(response.Body)
	if err != nil {
		return nil, err
	}

	unmarshalData, err := serializer.NewUnmarshalDataSeries().GetUnmarshal(body)
	if err != nil {
		return nil, err
	}
	dataSeries := unmarshalData.(data_lake.DataSeries)

	return &dataSeries, nil
}

func (d *DataLakeMeasure) ClearDataLakeMeasureData(measureName string) error {
	// Remove data from a single measurement series with given id

	endPointUrl := util.NewStreamPipesApiPath(d.config.Url, "streampipes-backend/api/v4/datalake/measurements", []string{measureName})
	log.Printf("Clear data from: %s", endPointUrl)
	response, err := d.executeRequest("DELETE", endPointUrl)
	if err != nil {
		return err
	}

	if response.StatusCode != http.StatusOK {
		err = d.handleStatusCode(response)
		if err != nil {
			return err
		}
	}
	log.Printf("Successfully deleted data from a single measurement sequence of %s", measureName)

	return nil
}

func (d *DataLakeMeasure) DeleteDataLakeMeasure(measureName string) error {
	// Drop a single measurement series with given id from Data Lake and remove related event property

	endPointUrl := util.NewStreamPipesApiPath(d.config.Url, "streampipes-backend/api/v4/datalake/measurements", []string{measureName, "drop"})
	log.Printf("Delete data from: %s", endPointUrl)
	response, err := d.executeRequest("DELETE", endPointUrl)
	if err != nil {
		return err
	}

	if response.StatusCode != http.StatusOK {
		err = d.handleStatusCode(response)
		if err != nil {
			return err
		}
	}

	log.Printf("Successfully dropped a single measurement series for %s from  DataLake and remove related event property", measureName)
	return nil
}
