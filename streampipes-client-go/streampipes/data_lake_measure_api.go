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
	"io"
	"log"
	"net/http"

	"github.com/apache/streampipes/streampipes-client-go/streampipes/config"
	"github.com/apache/streampipes/streampipes-client-go/streampipes/internal/serializer"
	"github.com/apache/streampipes/streampipes-client-go/streampipes/internal/util"
	"github.com/apache/streampipes/streampipes-client-go/streampipes/model/data_lake"
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

// GetAllDataLakeMeasure retrieves a list of all measurements series from the Data Lake.
func (d *DataLakeMeasure) GetAllDataLakeMeasure() ([]data_lake.DataLakeMeasure, error) {

	endPointUrl := util.NewStreamPipesApiPath(d.config.Url, "streampipes-backend/api/v4/datalake/measurements", nil)
	log.Printf("Get data from: %s", endPointUrl)

	response, err := d.executeRequest("GET", endPointUrl, nil)
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

	unmarshalData, err := serializer.NewDataLakeMeasuresDeserializer().Unmarshal(body)
	if err != nil {
		return nil, err
	}
	dataLakeMeasures := unmarshalData.([]data_lake.DataLakeMeasure)

	return dataLakeMeasures, nil
}

// DeleteDataLakeMeasurements removes all stored measurement series form Data Lake.
func (d *DataLakeMeasure) DeleteDataLakeMeasurements() error {

	endPointUrl := util.NewStreamPipesApiPath(d.config.Url, "streampipes-backend/api/v4/datalake/measurements", nil)
	log.Printf("Delete data from: %s", endPointUrl)

	response, err := d.executeRequest("DELETE", endPointUrl, nil)
	if err != nil {
		return err
	}

	if response.StatusCode != http.StatusOK {
		err = d.handleStatusCode(response)
		if err != nil {
			return err
		}
	}

	return nil
}

// GetSingleDataLakeMeasure retrieves a specific measure from the Data Lake.
func (d *DataLakeMeasure) GetSingleDataLakeMeasure(elementId string) (data_lake.DataLakeMeasure, error) {

	endPointUrl := util.NewStreamPipesApiPath(d.config.Url, "streampipes-backend/api/v4/datalake/measure", []string{elementId})
	log.Printf("Get data from: %s", endPointUrl)

	response, err := d.executeRequest("GET", endPointUrl, nil)
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

	unmarshalData, err := serializer.NewDataLakeMeasureDeserializer().Unmarshal(body)
	if err != nil {
		return data_lake.DataLakeMeasure{}, err
	}
	dataLakeMeasure := unmarshalData.(data_lake.DataLakeMeasure)

	return dataLakeMeasure, nil
}

// DeleteSingleDataLakeMeasure deletes a specific measure from the Data Lake.
func (d *DataLakeMeasure) DeleteSingleDataLakeMeasure(elementId string) error {

	endPointUrl := util.NewStreamPipesApiPath(d.config.Url, "streampipes-backend/api/v4/datalake/measure", []string{elementId})
	log.Printf("Delete data from: %s", endPointUrl)

	response, err := d.executeRequest("DELETE", endPointUrl, nil)
	if err != nil {
		return err
	}

	if response.StatusCode != http.StatusOK {
		err = d.handleStatusCode(response)
		if err != nil {
			return err
		}
	}

	return nil
}

// GetSingleDataSeries retrieves the measurement series for the specified measureId from the Data Lake.
// Currently not supporting parameter queries.
// The measureId can also be considered measureName.
func (d *DataLakeMeasure) GetSingleDataSeries(measureId string) (*data_lake.DataSeries, error) {

	endPointUrl := util.NewStreamPipesApiPath(d.config.Url, "streampipes-backend/api/v4/datalake/measurements", []string{measureId})
	log.Printf("Get data from: %s", endPointUrl)

	response, err := d.executeRequest("GET", endPointUrl, nil)
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

	unmarshalData, err := serializer.NewDataSeriesDeserializer().Unmarshal(body)
	if err != nil {
		return nil, err
	}
	dataSeries := unmarshalData.(data_lake.DataSeries)

	return &dataSeries, nil
}

// ClearDataLakeMeasureData removes data from a single measurement series with given id.
// The measureId can also be considered measureName.
func (d *DataLakeMeasure) ClearDataLakeMeasureData(measureId string) error {

	endPointUrl := util.NewStreamPipesApiPath(d.config.Url, "streampipes-backend/api/v4/datalake/measurements", []string{measureId})
	log.Printf("Clear data from: %s", endPointUrl)

	response, err := d.executeRequest("DELETE", endPointUrl, nil)
	if err != nil {
		return err
	}

	if response.StatusCode != http.StatusOK {
		err = d.handleStatusCode(response)
		if err != nil {
			return err
		}
	}
	log.Printf("Successfully deleted data from a single measurement sequence of %s", measureId)

	return nil
}

// DeleteDataLakeMeasure  drops a single measurement series with given id from Data Lake and remove related event property.
// The measureId can also be considered measureName.
func (d *DataLakeMeasure) DeleteDataLakeMeasure(measureId string) error {

	endPointUrl := util.NewStreamPipesApiPath(d.config.Url, "streampipes-backend/api/v4/datalake/measurements", []string{measureId, "drop"})
	log.Printf("Delete data from: %s", endPointUrl)
	response, err := d.executeRequest("DELETE", endPointUrl, nil)
	if err != nil {
		return err
	}

	if response.StatusCode != http.StatusOK {
		err = d.handleStatusCode(response)
		if err != nil {
			return err
		}
	}

	log.Printf("Successfully dropped a single measurement series for %s from  DataLake and remove related event property", measureId)
	return nil
}
