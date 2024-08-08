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
	"github.com/apache/streampipes/streampipes-client-go/streampipes/model/functions"
)

type Functions struct {
	endpoint
}

func NewFunctions(clientConfig config.StreamPipesClientConfig) *Functions {

	return &Functions{
		endpoint{config: clientConfig},
	}
}

func (f *Functions) GetFunctionLogs(functionId string) ([]functions.SpLogEntry, error) {

	endPointUrl := util.NewStreamPipesApiPath(f.config.Url, "streampipes-backend/api/v2/functions", []string{functionId, "logs"})
	log.Printf("Get data from: %s", endPointUrl)

	response, err := f.executeRequest("GET", endPointUrl, nil)
	if err != nil {
		return nil, err
	}

	if response.StatusCode != http.StatusOK {
		err = f.handleStatusCode(response)
		if err != nil {
			return nil, err
		}
	}

	body, err := io.ReadAll(response.Body)
	if err != nil {
		return nil, err
	}

	unmarshalData, err := serializer.NewSpLogEntriesDeserializer().Unmarshal(body)
	if err != nil {
		return nil, err
	}
	spLogEntry := unmarshalData.([]functions.SpLogEntry)

	return spLogEntry, nil
}

func (f *Functions) GetFunctionMetrics(functionId string) (functions.SpMetricsEntry, error) {

	endPointUrl := util.NewStreamPipesApiPath(f.config.Url, "streampipes-backend/api/v2/functions", []string{functionId, "metrics"})
	log.Printf("Get data from: %s", endPointUrl)

	response, err := f.executeRequest("GET", endPointUrl, nil)
	if err != nil {
		return functions.SpMetricsEntry{}, err
	}

	if response.StatusCode != http.StatusOK {
		err = f.handleStatusCode(response)
		if err != nil {
			return functions.SpMetricsEntry{}, err
		}
	}

	body, err := io.ReadAll(response.Body)
	if err != nil {
		return functions.SpMetricsEntry{}, err
	}

	unmarshalData, err := serializer.NewSpMetricsEntryDeserializer().Unmarshal(body)
	if err != nil {
		return functions.SpMetricsEntry{}, err
	}
	spLogEntry := unmarshalData.(functions.SpMetricsEntry)

	return spLogEntry, nil
}

func (f *Functions) GetAllFunction() ([]functions.FunctionDefinition, error) {

	endPointUrl := util.NewStreamPipesApiPath(f.config.Url, "streampipes-backend/api/v2/functions", nil)
	log.Printf("Get data from: %s", endPointUrl)

	response, err := f.executeRequest("GET", endPointUrl, nil)
	if err != nil {
		return nil, err
	}

	if response.StatusCode != http.StatusOK {
		err = f.handleStatusCode(response)
		if err != nil {
			return nil, err
		}
	}

	body, err := io.ReadAll(response.Body)
	if err != nil {
		return nil, err
	}

	unmarshalData, err := serializer.NewFunctionDefinitionsDeserializer().Unmarshal(body)
	if err != nil {
		return nil, err
	}
	functionDefinition := unmarshalData.([]functions.FunctionDefinition)

	return functionDefinition, nil
}

func (f *Functions) DeleteSingleFunction(functionId string) error {
	endPointUrl := util.NewStreamPipesApiPath(f.config.Url, "streampipes-backend/api/v2/functions", []string{functionId})
	log.Printf("Delete data from: %s", endPointUrl)

	response, err := f.executeRequest("DELETE", endPointUrl, nil)
	if err != nil {
		return err
	}

	if response.StatusCode != http.StatusOK {
		err = f.handleStatusCode(response)
		if err != nil {
			return err
		}
	}

	return nil
}
