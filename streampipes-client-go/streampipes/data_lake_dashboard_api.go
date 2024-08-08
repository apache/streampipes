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

type DataLakeDashboard struct {
	endpoint
}

func NewDataLakeDashborad(clientConfig config.StreamPipesClientConfig) *DataLakeDashboard {

	return &DataLakeDashboard{
		endpoint{config: clientConfig},
	}
}

func (d *DataLakeDashboard) GetSingleDataLakeDashboard(dashboardId string) (data_lake.Dashboard, error) {
	endPointUrl := util.NewStreamPipesApiPath(d.config.Url, "streampipes-backend/api/v3/datalake/dashboard", []string{dashboardId})
	log.Printf("Get data from: %s", endPointUrl)

	response, err := d.executeRequest("GET", endPointUrl, nil)
	if err != nil {
		return data_lake.Dashboard{}, err
	}

	if response.StatusCode != http.StatusOK {
		err = d.handleStatusCode(response)
		if err != nil {
			return data_lake.Dashboard{}, err
		}
	}

	body, err := io.ReadAll(response.Body)
	if err != nil {
		return data_lake.Dashboard{}, err
	}

	unmarshalData, err := serializer.NewDataLakeDashboardDeserializer().Unmarshal(body)
	if err != nil {
		return data_lake.Dashboard{}, err
	}
	dataLakeDashboard := unmarshalData.(data_lake.Dashboard)

	return dataLakeDashboard, nil
}

func (d *DataLakeDashboard) GetAllDataLakeDashboard() ([]data_lake.Dashboard, error) {
	endPointUrl := util.NewStreamPipesApiPath(d.config.Url, "streampipes-backend/api/v3/datalake/dashboard", nil)
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

	unmarshalData, err := serializer.NewDataLakeDashboardsDeserializer().Unmarshal(body)
	if err != nil {
		return nil, err
	}
	dataLakeDashboard := unmarshalData.([]data_lake.Dashboard)

	return dataLakeDashboard, nil
}

func (d *DataLakeDashboard) DeleteSingleDataLakeDashboard(dashboardId string) error {
	endPointUrl := util.NewStreamPipesApiPath(d.config.Url, "streampipes-backend/api/v3/datalake/dashboard", []string{dashboardId})
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
