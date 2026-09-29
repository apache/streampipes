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

const (
	datasetMetadataPath = "streampipes-backend/api/v4/datalake/measure"
	datasetDataPath     = "streampipes-backend/api/v4/datalake/measurements"
)

// Dataset connects to the dataset endpoints of StreamPipes.
// It provides access to the metadata of datasets (see GetAllDatasetMetadata, GetSingleDatasetMetadata)
// as well as to the data stored in a dataset (see GetSingleDataSeries).
// It supersedes the deprecated DataLakeMeasure API.
type Dataset struct {
	endpoint
}

// NewDatasets is used to return an instance of *Dataset.
func NewDatasets(clientConfig config.StreamPipesClientConfig) *Dataset {

	return &Dataset{
		endpoint{config: clientConfig},
	}
}

// GetAllDatasetMetadata retrieves the metadata of all datasets.
func (d *Dataset) GetAllDatasetMetadata() ([]data_lake.DatasetMetadata, error) {

	endPointUrl := util.NewStreamPipesApiPath(d.config.Url, datasetDataPath, nil)
	log.Printf("Get data from: %s", endPointUrl)

	body, err := d.readBody("GET", endPointUrl)
	if err != nil {
		return nil, err
	}

	unmarshalData, err := serializer.NewDatasetMetadataListDeserializer().Unmarshal(body)
	if err != nil {
		return nil, err
	}
	datasets := unmarshalData.([]data_lake.DatasetMetadata)

	return datasets, nil
}

// DeleteAllDatasets removes all stored datasets.
func (d *Dataset) DeleteAllDatasets() error {

	endPointUrl := util.NewStreamPipesApiPath(d.config.Url, datasetDataPath, nil)
	log.Printf("Delete data from: %s", endPointUrl)

	return d.executeWithoutBody("DELETE", endPointUrl)
}

// GetSingleDatasetMetadata retrieves the metadata of a specific dataset by its element id.
func (d *Dataset) GetSingleDatasetMetadata(elementId string) (data_lake.DatasetMetadata, error) {

	endPointUrl := util.NewStreamPipesApiPath(d.config.Url, datasetMetadataPath, []string{elementId})
	log.Printf("Get data from: %s", endPointUrl)

	body, err := d.readBody("GET", endPointUrl)
	if err != nil {
		return data_lake.DatasetMetadata{}, err
	}

	unmarshalData, err := serializer.NewDatasetMetadataDeserializer().Unmarshal(body)
	if err != nil {
		return data_lake.DatasetMetadata{}, err
	}
	dataset := unmarshalData.(data_lake.DatasetMetadata)

	return dataset, nil
}

// DeleteSingleDatasetMetadata deletes the metadata of a specific dataset by its element id.
func (d *Dataset) DeleteSingleDatasetMetadata(elementId string) error {

	endPointUrl := util.NewStreamPipesApiPath(d.config.Url, datasetMetadataPath, []string{elementId})
	log.Printf("Delete data from: %s", endPointUrl)

	return d.executeWithoutBody("DELETE", endPointUrl)
}

// GetSingleDataSeries retrieves the data stored in the dataset with the given id.
// Currently not supporting parameter queries.
// The datasetId can also be considered the dataset's measureName.
func (d *Dataset) GetSingleDataSeries(datasetId string) (*data_lake.DataSeries, error) {

	endPointUrl := util.NewStreamPipesApiPath(d.config.Url, datasetDataPath, []string{datasetId})
	log.Printf("Get data from: %s", endPointUrl)

	body, err := d.readBody("GET", endPointUrl)
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

// ClearDatasetData removes the data stored in the dataset with the given id while keeping the dataset itself.
// The datasetId can also be considered the dataset's measureName.
func (d *Dataset) ClearDatasetData(datasetId string) error {

	endPointUrl := util.NewStreamPipesApiPath(d.config.Url, datasetDataPath, []string{datasetId})
	log.Printf("Clear data from: %s", endPointUrl)

	if err := d.executeWithoutBody("DELETE", endPointUrl); err != nil {
		return err
	}
	log.Printf("Successfully deleted the data of dataset %s", datasetId)

	return nil
}

// DeleteDataset drops the dataset with the given id including its data and metadata.
// The datasetId can also be considered the dataset's measureName.
func (d *Dataset) DeleteDataset(datasetId string) error {

	endPointUrl := util.NewStreamPipesApiPath(d.config.Url, datasetDataPath, []string{datasetId, "drop"})
	log.Printf("Delete data from: %s", endPointUrl)

	if err := d.executeWithoutBody("DELETE", endPointUrl); err != nil {
		return err
	}
	log.Printf("Successfully dropped dataset %s", datasetId)

	return nil
}

// readBody executes the request and returns the response body for a successful response.
func (d *Dataset) readBody(method string, endPointUrl string) ([]byte, error) {

	response, err := d.executeRequest(method, endPointUrl, nil)
	if err != nil {
		return nil, err
	}
	defer response.Body.Close()

	if response.StatusCode != http.StatusOK {
		err = d.handleStatusCode(response)
		if err != nil {
			return nil, err
		}
	}

	return io.ReadAll(response.Body)
}

// executeWithoutBody executes the request and only evaluates the response status.
func (d *Dataset) executeWithoutBody(method string, endPointUrl string) error {

	response, err := d.executeRequest(method, endPointUrl, nil)
	if err != nil {
		return err
	}
	defer response.Body.Close()

	if response.StatusCode != http.StatusOK {
		return d.handleStatusCode(response)
	}

	return nil
}
