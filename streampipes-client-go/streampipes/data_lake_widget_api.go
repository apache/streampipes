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

type DataLakeWidget struct {
	endpoint
}

func NewDataLakeWidget(clientConfig config.StreamPipesClientConfig) *DataLakeWidget {

	return &DataLakeWidget{
		endpoint{config: clientConfig},
	}
}

func (d *DataLakeWidget) GetSingleDataLakeWidget(widgetId string) (data_lake.DataExplorerWidgetModel, error) {

	endPointUrl := util.NewStreamPipesApiPath(d.config.Url, "streampipes-backend/api/v3/datalake/dashboard/widgets", []string{widgetId})
	log.Printf("Get data from: %s", endPointUrl)

	response, err := d.executeRequest("GET", endPointUrl, nil)
	if err != nil {
		return data_lake.DataExplorerWidgetModel{}, err
	}

	if response.StatusCode != http.StatusOK {
		err = d.handleStatusCode(response)
		if err != nil {
			return data_lake.DataExplorerWidgetModel{}, err
		}
	}

	body, err := io.ReadAll(response.Body)
	if err != nil {
		return data_lake.DataExplorerWidgetModel{}, err
	}

	unmarshalData, err := serializer.NewDataLakeWidgetDeserializer().Unmarshal(body)
	if err != nil {
		return data_lake.DataExplorerWidgetModel{}, err
	}
	dataLakeWidget := unmarshalData.(data_lake.DataExplorerWidgetModel)

	return dataLakeWidget, nil
}

func (d *DataLakeWidget) GetAllDataLakeWidget() ([]data_lake.DataExplorerWidgetModel, error) {

	endPointUrl := util.NewStreamPipesApiPath(d.config.Url, "streampipes-backend/api/v3/datalake/dashboard/widgets", nil)
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

	unmarshalData, err := serializer.NewDataLakeWidgetsDeserializer().Unmarshal(body)
	if err != nil {
		return nil, err
	}
	dataLakeWidget := unmarshalData.([]data_lake.DataExplorerWidgetModel)

	return dataLakeWidget, nil
}

func (d *DataLakeWidget) DeleteSingleDataLakeWidget(widgetId string) error {
	endPointUrl := util.NewStreamPipesApiPath(d.config.Url, "streampipes-backend/api/v3/datalake/dashboard/widgets", []string{widgetId})
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
