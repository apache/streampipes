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

// GetSinglePipeline get a specific pipeline with the given id
func (p *Pipeline) GetSinglePipeline(pipelineId string) (pipeline.Pipeline, error) {

	endPointUrl := util.NewStreamPipesApiPath(p.config.Url, "streampipes-backend/api/v2/pipelines", []string{pipelineId})
	log.Printf("Get data from: %s", endPointUrl)

	response, err := p.executeRequest("GET", endPointUrl, nil)
	if err != nil {
		return pipeline.Pipeline{}, err
	}

	if response.StatusCode != http.StatusOK {
		err = p.handleStatusCode(response)
		if err != nil {
			return pipeline.Pipeline{}, err
		}
	}

	body, err := io.ReadAll(response.Body)
	if err != nil {
		return pipeline.Pipeline{}, err
	}

	unmarshalData, err := serializer.NewPipelineDeserializer().Unmarshal(body)
	if err != nil {
		return pipeline.Pipeline{}, err
	}
	pipeLine := unmarshalData.(pipeline.Pipeline)

	return pipeLine, nil
}

// DeleteSinglePipeline delete a pipeline with a given id
func (p *Pipeline) DeleteSinglePipeline(pipelineId string) error {

	endPointUrl := util.NewStreamPipesApiPath(p.config.Url, "streampipes-backend/api/v2/pipelines", []string{pipelineId})
	log.Printf("Delete data from: %s", endPointUrl)

	response, err := p.executeRequest("DELETE", endPointUrl, nil)
	if err != nil {
		return err
	}

	if response.StatusCode != http.StatusOK {
		err = p.handleStatusCode(response)
		if err != nil {
			return err
		}
	}
	return nil
}

// UpdateSinglePipeline update an existing pipeline
func (p *Pipeline) UpdateSinglePipeline(pp pipeline.Pipeline, pipelineId string) error {
	endPointUrl := util.NewStreamPipesApiPath(p.config.Url, "streampipes-backend/api/v2/pipelines", []string{pipelineId})
	body, err := serializer.NewPipelineSerializer().Marshal(pp)
	if err != nil {
		return err
	}
	response, err := p.executeRequest("PUT", endPointUrl, body)
	if err != nil {
		return err
	}

	if response.StatusCode != http.StatusOK {
		err = p.handleStatusCode(response)
		if err != nil {
			return err
		}
	}
	return nil
}

// GetAllPipeline get all pipelines of the current user
func (p *Pipeline) GetAllPipeline() ([]pipeline.Pipeline, error) {
	endPointUrl := util.NewStreamPipesApiPath(p.config.Url, "streampipes-backend/api/v2/pipelines", nil)
	log.Printf("Get data from: %s", endPointUrl)

	response, err := p.executeRequest("GET", endPointUrl, nil)
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

	unmarshalData, err := serializer.NewPipelinesDeserializer().Unmarshal(body)
	if err != nil {
		return nil, err
	}
	pipelines := unmarshalData.([]pipeline.Pipeline)

	return pipelines, nil
}

// CreatePipeline store a new pipeline
func (p *Pipeline) CreatePipeline(pp pipeline.Pipeline) error {
	endPointUrl := util.NewStreamPipesApiPath(p.config.Url, "streampipes-backend/api/v2/pipelines", nil)

	body, err := serializer.NewPipelineSerializer().Marshal(pp)
	if err != nil {
		return err
	}
	response, err := p.executeRequest("POST", endPointUrl, body)
	if err != nil {
		return err
	}

	if response.StatusCode != http.StatusOK {
		err = p.handleStatusCode(response)
		if err != nil {
			return err
		}
	}
	return nil
}

// StopSinglePipeline stop the pipeline with the given id
// 待定，因为不知道要返回什么
func (p *Pipeline) StopSinglePipeline(pipelineId string) error {
	endPointUrl := util.NewStreamPipesApiPath(p.config.Url, "streampipes-backend/api/v2/pipelines", []string{pipelineId, "stop"})

	response, err := p.executeRequest("GET", endPointUrl, nil)
	if err != nil {
		return err
	}

	if response.StatusCode != http.StatusOK {
		err = p.handleStatusCode(response)
		if err != nil {
			return err
		}
	}
	return nil
}

// GetSinglePipelineStatus get the pipeline status of a given pipeline
func (p *Pipeline) GetSinglePipelineStatus(pipelineId string) error {
	endPointUrl := util.NewStreamPipesApiPath(p.config.Url, "streampipes-backend/api/v2/pipelines", []string{pipelineId, "status"})

	response, err := p.executeRequest("GET", endPointUrl, nil)
	if err != nil {
		return err
	}

	if response.StatusCode != http.StatusOK {
		err = p.handleStatusCode(response)
		if err != nil {
			return err
		}
	}
	return nil
}

// StartSinglePipeline start the pipeline with the given id
func (p *Pipeline) StartSinglePipeline(pipelineId string) error {
	endPointUrl := util.NewStreamPipesApiPath(p.config.Url, "streampipes-backend/api/v2/pipelines", []string{pipelineId, "start"})

	response, err := p.executeRequest("GET", endPointUrl, nil)
	if err != nil {
		return err
	}

	if response.StatusCode != http.StatusOK {
		err = p.handleStatusCode(response)
		if err != nil {
			return err
		}
	}
	return nil
}

// ContainsElementPipeline returns all pipelines that contain the element with the elementld
func (p *Pipeline) ContainsElementPipeline(pipelineId string) error {
	endPointUrl := util.NewStreamPipesApiPath(p.config.Url, "streampipes-backend/api/v2/pipelines/contains", []string{pipelineId})

	response, err := p.executeRequest("GET", endPointUrl, nil)
	if err != nil {
		return err
	}

	if response.StatusCode != http.StatusOK {
		err = p.handleStatusCode(response)
		if err != nil {
			return err
		}
	}
	return nil
}

func (p *Pipeline) GetPipelineCategory() ([]pipeline.PipelineCategory, error) {

	endPointUrl := util.NewStreamPipesApiPath(p.config.Url, "streampipes-backend/api/v2/pipelinecategories", nil)
	log.Printf("Get data from: %s", endPointUrl)

	response, err := p.executeRequest("GET", endPointUrl, nil)
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

	response, err := p.executeRequest("DELETE", endPointUrl, nil)
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
