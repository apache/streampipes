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
	"log/slog"
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

// AllDataLakeMeasure retrieves all the measures from the Data Lake.
func (d *DataLakeMeasure) AllDataLakeMeasure() ([]data_lake.DataLakeMeasure, error) {

	endPointUrl := util.NewStreamPipesApiPath(d.config.Url, "streampipes-backend/api/v4/datalake/measurements", nil)
	slog.Info("Get data from: %s", endPointUrl)

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

	unmarshalData, err := serializer.NewDataLakeMeasuresDeserializer().Unmarshal(body)
	if err != nil {
		return nil, err
	}
	dataLakeMeasures := unmarshalData.([]data_lake.DataLakeMeasure)

	return dataLakeMeasures, nil
}

// GetSingleDataLakeMeasure retrieves a specific measure from the Data Lake.
func (d *DataLakeMeasure) GetSingleDataLakeMeasure(elementId string) (data_lake.DataLakeMeasure, error) {

	endPointUrl := util.NewStreamPipesApiPath(d.config.Url, "streampipes-backend/api/v4/datalake/measure", []string{elementId})
	slog.Info("Get data from: %s", endPointUrl)

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

	unmarshalData, err := serializer.NewDataLakeMeasureDeSerializer().Unmarshal(body)
	if err != nil {
		return data_lake.DataLakeMeasure{}, err
	}
	dataLakeMeasure := unmarshalData.(data_lake.DataLakeMeasure)

	return dataLakeMeasure, nil
}

// GetSingleDataSeries retrieves the measurement series for the specified measureId from the Data Lake.
// Currently not supporting parameter queries.
// The measureId can also be considered measureName.
func (d *DataLakeMeasure) GetSingleDataSeries(measureId string) (*data_lake.DataSeries, error) {

	endPointUrl := util.NewStreamPipesApiPath(d.config.Url, "streampipes-backend/api/v4/datalake/measurements", []string{measureId})
	slog.Info("Get data from: %s", endPointUrl)

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

	unmarshalData, err := serializer.NewDataSeriesDeSerializer().Unmarshal(body)
	if err != nil {
		return nil, err
	}
	dataSeries := unmarshalData.(data_lake.DataSeries)

	return &dataSeries, nil
}

// ClearDataLakeMeasureData removes data from a single measurement series with given id.
func (d *DataLakeMeasure) ClearDataLakeMeasureData(measureId string) error {

	endPointUrl := util.NewStreamPipesApiPath(d.config.Url, "streampipes-backend/api/v4/datalake/measurements", []string{measureId})
	slog.Info("Clear data from: %s", endPointUrl)

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
	slog.Info("Successfully deleted data from a single measurement sequence of %s", measureId)

	return nil
}

// DeleteDataLakeMeasure  drops a single measurement series with given id from Data Lake and remove related event property.
func (d *DataLakeMeasure) DeleteDataLakeMeasure(measureId string) error {

	endPointUrl := util.NewStreamPipesApiPath(d.config.Url, "streampipes-backend/api/v4/datalake/measurements", []string{measureId, "drop"})
	slog.Info("Delete data from: %s", endPointUrl)
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

	slog.Info("Successfully dropped a single measurement series for %s from  DataLake and remove related event property", measureId)
	return nil
}
