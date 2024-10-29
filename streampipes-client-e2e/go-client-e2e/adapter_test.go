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
	"testing"
)

const (
	E2E_ADAPTER_ID             = "e2e-adapter-id"
	E2E_ADAPTER_NAME           = "e2e-adapter-name"
	E2E_STREAM_REV             = "e2e-stream-rev"
	E2E_HOST_NAME              = "e2e-host-name"
	E2E_ADAPTER_OUT_TOPIC_NAME = "e2e-adapter-out-topic-name"
	E2E_PORT                   = "\"e2e-port\""
)

func TestCreateAdapter(t *testing.T) {
	TestDeleteAdapter(t)
	streamPipesClient, err := utils.CreateStreamPipesClient()
	if err != nil {
		t.Log(err)
		os.Exit(1)
	}
	data := utils.CreateData("adapter/machine.json")
	err = streamPipesClient.Adapter().CreateAdapter(data)
	if err != nil {
		t.Log(err)
		os.Exit(1)
	}
}

func TestGetAdapter(t *testing.T) {
	streamPipesClient, err := utils.CreateStreamPipesClient()
	if err != nil {
		t.Error(err)
		os.Exit(1)
	}
	adapters, err1 := streamPipesClient.Adapter().GetAllAdapter()
	if err1 != nil {
		t.Error(err1)
		os.Exit(1)
	}
	if len(adapters) == 0 {
		os.Exit(1)
	}
}

func TestStartAdapter(t *testing.T) {
	streamPipesClient, err := utils.CreateStreamPipesClient()
	if err != nil {
		t.Error(err)
		os.Exit(1)
	}
	adapters, err1 := streamPipesClient.Adapter().GetAllAdapter()
	if err1 != nil {
		t.Error(err1)
		os.Exit(1)
	}
	if len(adapters) == 0 {
		os.Exit(1)
	}
	err = streamPipesClient.Adapter().StartSingleAdapter(adapters[0].ElementID)
	if err != nil {
		t.Error(err1)
		os.Exit(1)
	}
}

func TestStopAdapter(t *testing.T) {
	streamPipesClient, err := utils.CreateStreamPipesClient()
	if err != nil {
		t.Error(err)
		os.Exit(1)
	}
	adapters, err1 := streamPipesClient.Adapter().GetAllAdapter()
	if err1 != nil {
		t.Error(err1)
		os.Exit(1)
	}
	for _, adapter := range adapters {
		err = streamPipesClient.Adapter().StopSingleAdapter(adapter.ElementID)
		if err != nil {
			t.Error(err)
			os.Exit(1)
		}
		adapter, err = streamPipesClient.Adapter().GetSingleAdapter(adapter.ElementID)
		if err != nil || adapter.Running {
			t.Error(err)
			os.Exit(1)
		}
	}

}

func TestDeleteAdapter(t *testing.T) {
	streamPipesClient, err := utils.CreateStreamPipesClient()
	if err != nil {
		t.Error(err)
		os.Exit(1)
	}
	adapters, err1 := streamPipesClient.Adapter().GetAllAdapter()
	if err1 != nil {
		t.Error(err1)
		os.Exit(1)
	}
	for _, adapter := range adapters {
		err = streamPipesClient.Adapter().DeleteSingleAdapter(adapter.ElementID)
		if err != nil {
			t.Error(err1)
			os.Exit(1)
		}
	}

}
