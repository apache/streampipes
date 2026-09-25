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
	"net/http"
	"net/http/httptest"
	"testing"

	"github.com/apache/streampipes/streampipes-client-go/streampipes/config"
)

const datasetMetadataJson = `{
  "@class": "org.apache.streampipes.model.dataset.DatasetMetadata",
  "elementId": "abc",
  "measureName": "flowrate",
  "timestampField": "s0::timestamp",
  "eventSchema": {"eventProperties": []},
  "pipelineId": "p1",
  "pipelineName": "pipe",
  "schemaVersion": "1.1",
  "schemaUpdateStrategy": "UPDATE_SCHEMA",
  "retentionTime": null
}`

func newDatasetTestServer(t *testing.T, expectedMethod string, expectedPath string, body string) *httptest.Server {
	t.Helper()
	return httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.Method != expectedMethod || r.URL.Path != expectedPath {
			t.Errorf("unexpected request %s %s, expected %s %s", r.Method, r.URL.Path, expectedMethod, expectedPath)
			w.WriteHeader(http.StatusNotFound)
			return
		}
		w.Header().Set("Content-Type", "application/json")
		_, _ = w.Write([]byte(body))
	}))
}

func newDatasetApi(serverUrl string) *Dataset {
	return NewDatasets(config.NewStreamPipesClientConnectConfig(serverUrl, config.NewStreamPipesApiKeyCredentials("user", "key")))
}

func TestGetAllDatasetMetadata(t *testing.T) {
	server := newDatasetTestServer(t, "GET", "/streampipes-backend/api/v4/datalake/measurements", "["+datasetMetadataJson+"]")
	defer server.Close()

	datasets, err := newDatasetApi(server.URL).GetAllDatasetMetadata()
	if err != nil {
		t.Fatal(err)
	}
	if len(datasets) != 1 {
		t.Fatalf("expected 1 dataset, got %d", len(datasets))
	}
	if datasets[0].MeasureName != "flowrate" || datasets[0].SchemaUpdateStrategy != "UPDATE_SCHEMA" || datasets[0].ElementId != "abc" {
		t.Errorf("unexpected dataset metadata: %+v", datasets[0])
	}
}

func TestGetSingleDatasetMetadata(t *testing.T) {
	server := newDatasetTestServer(t, "GET", "/streampipes-backend/api/v4/datalake/measure/abc", datasetMetadataJson)
	defer server.Close()

	dataset, err := newDatasetApi(server.URL).GetSingleDatasetMetadata("abc")
	if err != nil {
		t.Fatal(err)
	}
	if dataset.MeasureName != "flowrate" || dataset.TimestampField != "s0::timestamp" {
		t.Errorf("unexpected dataset metadata: %+v", dataset)
	}
}

func TestDeleteDataset(t *testing.T) {
	server := newDatasetTestServer(t, "DELETE", "/streampipes-backend/api/v4/datalake/measurements/flowrate/drop", "")
	defer server.Close()

	if err := newDatasetApi(server.URL).DeleteDataset("flowrate"); err != nil {
		t.Fatal(err)
	}
}

func TestDatasetApiReturnsErrorOnFailure(t *testing.T) {
	server := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.WriteHeader(http.StatusUnauthorized)
	}))
	defer server.Close()

	if _, err := newDatasetApi(server.URL).GetAllDatasetMetadata(); err == nil {
		t.Error("expected an error for an unauthorized response")
	}
}
