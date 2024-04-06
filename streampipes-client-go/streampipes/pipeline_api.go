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
	"github.com/apache/streampipes/streampipes-client-go/streampipes/model"
	"github.com/apache/streampipes/streampipes-client-go/streampipes/model/pipeline"
	"io"
	"log"
	"net/http"
)

type Pipeline struct {
	endpoint
}

func NewPipeline(clientConfig config.StreamPipesClientConfig) *Pipeline {

	return &Pipeline{
		endpoint{config: clientConfig},
	}
}

func (p *Pipeline) GetPipelineCategory() ([]pipeline.PipelineCategory, error) {

	endPointUrl := util.NewStreamPipesApiPath(p.config.Url, "streampipes-backend/api/v2/pipelinecategories", nil)
	log.Printf("Get data from: %s", endPointUrl)

	response, err := p.executeRequest("GET", endPointUrl)
	if err != nil {
		return nil, err
	}

	if response.StatusCode != http.StatusOK {
		err = p.handleStatusCode(response)
		if err != nil {
			return nil, err
		}
	}

	body, err := io.ReadAll(response.Body)
	if err != nil {
		return nil, err
	}

	unmarshalData, err := serializer.NewPipelineCategoriesDeserializer().Unmarshal(body)
	if err != nil {
		return nil, err
	}
	pipelineCategory := unmarshalData.([]pipeline.PipelineCategory)

	return pipelineCategory, nil
}

func (p *Pipeline) DeletePipelineCategory(categoryId string) (model.ResponseMessage, error) {

	endPointUrl := util.NewStreamPipesApiPath(p.config.Url, "streampipes-backend/api/v2/pipelinecategories", []string{categoryId})
	log.Printf("Delete data from: %s", endPointUrl)

	response, err := p.executeRequest("DELETE", endPointUrl)
	if err != nil {
		return model.ResponseMessage{}, err
	}

	if response.StatusCode != http.StatusOK {
		err = p.handleStatusCode(response)
		if err != nil {
			return model.ResponseMessage{}, err
		}
	}

	body, err := io.ReadAll(response.Body)
	if err != nil {
		return model.ResponseMessage{}, err
	}

	unmarshalData, err := serializer.NewResponseMessageDeserializer().Unmarshal(body)
	if err != nil {
		return model.ResponseMessage{}, err
	}
	responseMessage := unmarshalData.(model.ResponseMessage)

	return responseMessage, nil
}
