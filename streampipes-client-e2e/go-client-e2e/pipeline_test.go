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

package go_client_e2e

import (
	"go-client-e2e/utils"
	"os"
	"strings"
	"testing"
)

func TestCreatePipeline(t *testing.T) {
	TestDeletePipeline(t)
	TestCreateAdapter(t)

	streamPipesClient, err := utils.CreateStreamPipesClient()
	if err != nil {
		os.Exit(1)
	}
	TestStartAdapter(t)
	adapters, err := streamPipesClient.Adapter().GetAllAdapter()
	if err != nil {
		t.Error(err)
		os.Exit(1)
	}
	if len(adapters) == 0 {
		os.Exit(1)
	}
	adapter := adapters[0]

	data := utils.CreateData("pipelines/pipelines.json")
	pipeline := string(data)
	pipeline = strings.Replace(pipeline, E2E_ADAPTER_ID, adapter.ElementID, -1)
	pipeline = strings.Replace(pipeline, E2E_ADAPTER_NAME, adapter.Name, -1)
	pipeline = strings.Replace(pipeline, E2E_STREAM_REV, adapter.Rev, -1)
	pipeline = strings.Replace(pipeline, E2E_ADAPTER_OUT_TOPIC_NAME, adapter.EventGrounding.TransportProtocols[0].TopicDefinition.ActualTopicName, -1)

	message, statusErr := streamPipesClient.Pipeline().CreatePipeline([]byte(pipeline))
	if statusErr != nil || !message.Success {
		os.Exit(1)
	}

}

func TestGetPipeline(t *testing.T) {
	streamPipesClient, err := utils.CreateStreamPipesClient()
	if err != nil {
		t.Error(err)
		os.Exit(1)
	}
	pipelines, err1 := streamPipesClient.Pipeline().GetAllPipeline()
	if err1 != nil {
		t.Error(err1)
		os.Exit(1)
	}
	if len(pipelines) == 0 {
		os.Exit(1)
	}
}

func TestStartPipeline(t *testing.T) {
	streamPipesClient, err := utils.CreateStreamPipesClient()
	if err != nil {
		t.Error(err)
		os.Exit(1)
	}
	pipelines, err1 := streamPipesClient.Pipeline().GetAllPipeline()
	if err1 != nil {
		t.Error(err1)
		os.Exit(1)
	}
	if len(pipelines) == 0 {
		os.Exit(1)
	}

	status, statusErr := streamPipesClient.Pipeline().StartSinglePipeline(pipelines[0].PipelineId)
	if statusErr != nil || !status.Success {
		t.Error(status.Title)
		os.Exit(1)
	}
}

func TestStopPipeline(t *testing.T) {
	streamPipesClient, err := utils.CreateStreamPipesClient()
	if err != nil {
		t.Error(err)
		os.Exit(1)
	}
	pipelines, err1 := streamPipesClient.Pipeline().GetAllPipeline()
	for _, pipeline := range pipelines {
		operation, operationErr := streamPipesClient.Pipeline().StopSinglePipeline(pipeline.PipelineId)
		if operationErr != nil || !operation.Success {
			t.Error(err1)
			os.Exit(1)
		}
	}

}

func TestDeletePipeline(t *testing.T) {
	streamPipesClient, err := utils.CreateStreamPipesClient()
	if err != nil {
		t.Error(err)
		os.Exit(1)
	}
	pipelines, err1 := streamPipesClient.Pipeline().GetAllPipeline()
	if err1 != nil {
		t.Error(err1)
		os.Exit(1)
	}
	for _, pipeline := range pipelines {
		err = streamPipesClient.Pipeline().DeleteSinglePipeline(pipeline.PipelineId)
		if err != nil {
			t.Error(err1)
			os.Exit(1)
		}
	}
	TestDeleteAdapter(t)
}
