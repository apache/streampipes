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
	"github.com/apache/streampipes/streampipes-client-go/streampipes/model/adapter"
	"io"
	"log"
	"net/http"
)

type Adapter struct {
	endpoint
}

func NewAdapter(clientConfig config.StreamPipesClientConfig) *Adapter {

	return &Adapter{
		endpoint{config: clientConfig},
	}
}

// GetSingleAdapter get a specific adapter with the given id
func (a *Adapter) GetSingleAdapter(adapterId string) (adapter.AdapterDescription, error) {

	endPointUrl := util.NewStreamPipesApiPath(a.config.Url, "streampipes-backend/api/v2/connect/master/adapters", []string{adapterId})
	log.Printf("Get data from: %s", endPointUrl)

	response, err := a.executeRequest("GET", endPointUrl, nil)
	if err != nil {
		return adapter.AdapterDescription{}, err
	}

	if response.StatusCode != http.StatusOK {
		err = a.handleStatusCode(response)
		if err != nil {
			return adapter.AdapterDescription{}, err
		}
	}

	body, err := io.ReadAll(response.Body)
	if err != nil {
		return adapter.AdapterDescription{}, err
	}

	unmarshalData, err := serializer.NewAdapterDeserializer().Unmarshal(body)
	if err != nil {
		return adapter.AdapterDescription{}, err
	}
	adapterDescription := unmarshalData.(adapter.AdapterDescription)

	return adapterDescription, nil
}

// GetAllAdapter get all adapters of the current user
func (a *Adapter) GetAllAdapter() ([]adapter.AdapterDescription, error) {
	endPointUrl := util.NewStreamPipesApiPath(a.config.Url, "streampipes-backend/api/v2/connect/master/adapters", nil)

	response, err := a.executeRequest("GET", endPointUrl, nil)
	if err != nil {
		return nil, err
	}

	if response.StatusCode != http.StatusOK {
		err = a.handleStatusCode(response)
		if err != nil {
			return nil, err
		}
	}

	body, err := io.ReadAll(response.Body)
	if err != nil {
		return nil, err
	}

	unmarshalData, err := serializer.NewAdaptersDeserializer().Unmarshal(body)
	if err != nil {
		return nil, err
	}
	adapters := unmarshalData.([]adapter.AdapterDescription)

	return adapters, nil
}

// DeleteSingleAdapter delete a adapter with a given id
func (a *Adapter) DeleteSingleAdapter(adapterId string) error {

	endPointUrl := util.NewStreamPipesApiPath(a.config.Url, "streampipes-backend/api/v2/connect/master/adapters", []string{adapterId})
	log.Printf("Delete data from: %s", endPointUrl)

	response, err := a.executeRequest("DELETE", endPointUrl, nil)
	if err != nil {
		return err
	}

	if response.StatusCode != http.StatusOK {
		err = a.handleStatusCode(response)
		if err != nil {
			return err
		}
	}
	return nil
}

func (a *Adapter) CreateAdapter(adapters adapter.AdapterDescription) error {
	endPointUrl := util.NewStreamPipesApiPath(a.config.Url, "streampipes-backend/api/v2/connect/master/adapters", nil)
	body, err := serializer.NewAdapterSerializer().Marshal(adapters)
	if err != nil {
		return err
	}
	response, err := a.executeRequest("PUT", endPointUrl, body)
	if err != nil {
		return err
	}

	if response.StatusCode != http.StatusOK {
		err = a.handleStatusCode(response)
		if err != nil {
			return err
		}
	}
	return nil
}

func (a *Adapter) StopSingleAdapter(adapterId string) error {
	endPointUrl := util.NewStreamPipesApiPath(a.config.Url, "streampipes-backend/api/v2/connect/master/adapters", []string{adapterId, "stop"})

	response, err := a.executeRequest("POST", endPointUrl, nil)
	if err != nil {
		return err
	}
	if response.StatusCode != http.StatusOK {
		err = a.handleStatusCode(response)
		if err != nil {
			return err
		}
	}

	return nil
}

func (a *Adapter) StartSingleAdapter(adapterId string) error {
	endPointUrl := util.NewStreamPipesApiPath(a.config.Url, "streampipes-backend/api/v2/pipelines", []string{adapterId, "start"})

	response, err := a.executeRequest("POST", endPointUrl, nil)
	if err != nil {
		return err
	}
	if response.StatusCode != http.StatusOK {
		err = a.handleStatusCode(response)
		if err != nil {
			return err
		}
	}

	return nil
}
