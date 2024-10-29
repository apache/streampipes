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
	"time"
)

func TestGetDataLake(t *testing.T) {
	TestCreatePipeline(t)
	TestStartPipeline(t)
	streamPipesClient, err := utils.CreateStreamPipesClient()
	if err != nil {
		t.Error(err)
		os.Exit(1)
	}
	dataLake := streamPipesClient.DataLakeMeasures()
	measures, err := dataLake.GetAllDataLakeMeasure()
	if err != nil {
		t.Error(err)
		os.Exit(1)
	}

	time.Sleep(10 * time.Second)

	if len(measures) == 0 {
		TestStopPipeline(t)
		TestDeletePipeline(t)
		t.Error("No data lake measures found")
		os.Exit(1)
	}
	TestStopPipeline(t)
	TestDeletePipeline(t)
}
