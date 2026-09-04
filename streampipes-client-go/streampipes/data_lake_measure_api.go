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

// DatasetMeasure connects to the dataset measure endpoint of StreamPipes.
// DatasetMeasure supports GET and DELETE to delete or obtain resources.
// The specific interaction behavior is provided by the method bound to the DatasetMeasure struct.
type DatasetMeasure struct {
	endpoint
}

// Deprecated: use DatasetMeasure instead.
type DataLakeMeasure = DatasetMeasure

func NewDatasetMeasures(clientConfig config.StreamPipesClientConfig) *DatasetMeasure {
	// NewDatasetMeasures is used to return an instance of *DatasetMeasure.

	return &DatasetMeasure{
		endpoint{config: clientConfig},
	}
}

// Deprecated: use NewDatasetMeasures instead.
func NewDataLakeMeasures(clientConfig config.StreamPipesClientConfig) *DatasetMeasure {
	return NewDatasetMeasures(clientConfig)
}

// GetAllDatasetMeasures retrieves a list of all measurement series from the dataset storage.
func (d *DatasetMeasure) GetAllDatasetMeasures() ([]data_lake.DataLakeMeasure, error) {

	endPointUrl := util.NewStreamPipesApiPath(d.config.Url, "streampipes-backend/api/v4/dataset/measurements", nil)
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

// Deprecated: use GetAllDatasetMeasures instead.
func (d *DatasetMeasure) GetAllDataLakeMeasure() ([]data_lake.DataLakeMeasure, error) {
	return d.GetAllDatasetMeasures()
}

// DeleteDatasetMeasures removes all stored measurement series from the dataset storage.
func (d *DatasetMeasure) DeleteDatasetMeasures() error {

	endPointUrl := util.NewStreamPipesApiPath(d.config.Url, "streampipes-backend/api/v4/dataset/measurements", nil)
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

// Deprecated: use DeleteDatasetMeasures instead.
func (d *DatasetMeasure) DeleteDataLakeMeasurements() error {
	return d.DeleteDatasetMeasures()
}

// GetSingleDatasetMeasure retrieves a specific measure from the dataset storage.
func (d *DatasetMeasure) GetSingleDatasetMeasure(elementId string) (data_lake.DataLakeMeasure, error) {

	endPointUrl := util.NewStreamPipesApiPath(d.config.Url, "streampipes-backend/api/v4/dataset/measure", []string{elementId})
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

// Deprecated: use GetSingleDatasetMeasure instead.
func (d *DatasetMeasure) GetSingleDataLakeMeasure(elementId string) (data_lake.DataLakeMeasure, error) {
	return d.GetSingleDatasetMeasure(elementId)
}

// DeleteSingleDatasetMeasure deletes a specific measure from the dataset storage.
func (d *DatasetMeasure) DeleteSingleDatasetMeasure(elementId string) error {

	endPointUrl := util.NewStreamPipesApiPath(d.config.Url, "streampipes-backend/api/v4/dataset/measure", []string{elementId})
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

// Deprecated: use DeleteSingleDatasetMeasure instead.
func (d *DatasetMeasure) DeleteSingleDataLakeMeasure(elementId string) error {
	return d.DeleteSingleDatasetMeasure(elementId)
}

// GetSingleDatasetSeries retrieves the measurement series for the specified measureId from the dataset storage.
// Currently not supporting parameter queries.
// The measureId can also be considered measureName.
func (d *DatasetMeasure) GetSingleDatasetSeries(measureId string) (*data_lake.DataSeries, error) {

	endPointUrl := util.NewStreamPipesApiPath(d.config.Url, "streampipes-backend/api/v4/dataset/measurements", []string{measureId})
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

// Deprecated: use GetSingleDatasetSeries instead.
func (d *DatasetMeasure) GetSingleDataSeries(measureId string) (*data_lake.DataSeries, error) {
	return d.GetSingleDatasetSeries(measureId)
}

// ClearDatasetMeasureData removes data from a single measurement series with given id.
// The measureId can also be considered measureName.
func (d *DatasetMeasure) ClearDatasetMeasureData(measureId string) error {

	endPointUrl := util.NewStreamPipesApiPath(d.config.Url, "streampipes-backend/api/v4/dataset/measurements", []string{measureId})
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

// Deprecated: use ClearDatasetMeasureData instead.
func (d *DatasetMeasure) ClearDataLakeMeasureData(measureId string) error {
	return d.ClearDatasetMeasureData(measureId)
}

// DeleteDatasetMeasure drops a single measurement series with given id from the dataset storage and removes the
// related event property.
// The measureId can also be considered measureName.
func (d *DatasetMeasure) DeleteDatasetMeasure(measureId string) error {

	endPointUrl := util.NewStreamPipesApiPath(d.config.Url, "streampipes-backend/api/v4/dataset/measurements", []string{measureId, "drop"})
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

// Deprecated: use DeleteDatasetMeasure instead.
func (d *DatasetMeasure) DeleteDataLakeMeasure(measureId string) error {
	return d.DeleteDatasetMeasure(measureId)
}
