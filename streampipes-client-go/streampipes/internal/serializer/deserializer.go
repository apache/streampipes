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

package serializer

import (
	"encoding/json"
	"github.com/apache/streampipes/streampipes-client-go/streampipes/model/adapter"
	"log"
	"strings"

	"github.com/apache/streampipes/streampipes-client-go/streampipes/model"
	"github.com/apache/streampipes/streampipes-client-go/streampipes/model/data_lake"

	"github.com/apache/streampipes/streampipes-client-go/streampipes/model/pipeline"

	"github.com/apache/streampipes/streampipes-client-go/streampipes/model/functions"

	"github.com/apache/streampipes/streampipes-client-go/streampipes/model/streampipes_user"

	"github.com/apache/streampipes/streampipes-client-go/streampipes/model/streampipes_version"
)

type Deserializer interface {
	Unmarshal(body []byte) (interface{}, error)
}

type DataLakeMeasuresDeserializer struct{}

func NewDataLakeMeasuresDeserializer() *DataLakeMeasuresDeserializer {
	return &DataLakeMeasuresDeserializer{}
}

func (d DataLakeMeasuresDeserializer) Unmarshal(data []byte) (interface{}, error) {
	var dataLakeMeasures []data_lake.DataLakeMeasure
	dec := json.NewDecoder(strings.NewReader(string(data)))
	dec.DisallowUnknownFields()
	if err := dec.Decode(&dataLakeMeasures); err != nil && !strings.Contains(err.Error(), "unknown field") {
		log.Println(err)
		return nil, err
	}
	return dataLakeMeasures, nil
}

type DataLakeMeasureDeserializer struct{}

func NewDataLakeMeasureDeserializer() *DataLakeMeasureDeserializer {
	return &DataLakeMeasureDeserializer{}
}

func (d DataLakeMeasureDeserializer) Unmarshal(data []byte) (interface{}, error) {
	var dataLakeMeasure data_lake.DataLakeMeasure
	dec := json.NewDecoder(strings.NewReader(string(data)))
	dec.DisallowUnknownFields()
	if err := dec.Decode(&dataLakeMeasure); err != nil && !strings.Contains(err.Error(), "unknown field") {
		log.Println(err)
		return nil, err
	}
	return dataLakeMeasure, nil
}

type DataSeriesDeserializer struct{}

func NewDataSeriesDeserializer() *DataSeriesDeserializer {
	return &DataSeriesDeserializer{}
}

func (d DataSeriesDeserializer) Unmarshal(data []byte) (interface{}, error) {
	var dataSeries data_lake.DataSeries
	dec := json.NewDecoder(strings.NewReader(string(data)))
	dec.DisallowUnknownFields()
	if err := dec.Decode(&dataSeries); err != nil && !strings.Contains(err.Error(), "unknown field") {
		log.Println(err)
		return nil, err
	}
	return dataSeries, nil
}

type StreamPipesVersionDeserializer struct{}

func NewStreamPipesVersionDeserializer() *StreamPipesVersionDeserializer {
	return &StreamPipesVersionDeserializer{}
}

func (d StreamPipesVersionDeserializer) Unmarshal(data []byte) (interface{}, error) {
	var version streampipes_version.Versions
	dec := json.NewDecoder(strings.NewReader(string(data)))
	dec.DisallowUnknownFields()
	if err := dec.Decode(&version); err != nil && !strings.Contains(err.Error(), "unknown field") {
		log.Println(err)
		return nil, err
	}
	return version, nil
}

type ResponseMessageDeserializer struct{}

func NewResponseMessageDeserializer() *ResponseMessageDeserializer {
	return &ResponseMessageDeserializer{}
}

func (r ResponseMessageDeserializer) Unmarshal(data []byte) (interface{}, error) {
	var responseMessage model.ResponseMessage
	dec := json.NewDecoder(strings.NewReader(string(data)))
	dec.DisallowUnknownFields()
	if err := dec.Decode(&responseMessage); err != nil && !strings.Contains(err.Error(), "unknown field") {
		log.Println(err)
		return nil, err
	}
	return responseMessage, nil
}

type PipelineCategoriesDeserializer struct{}

func NewPipelineCategoriesDeserializer() *PipelineCategoriesDeserializer {
	return &PipelineCategoriesDeserializer{}
}

func (p PipelineCategoriesDeserializer) Unmarshal(data []byte) (interface{}, error) {
	var pipelineCategory []pipeline.PipelineCategory

	dec := json.NewDecoder(strings.NewReader(string(data)))
	dec.DisallowUnknownFields()

	if err := dec.Decode(&pipelineCategory); err != nil && !strings.Contains(err.Error(), "unknown field") {
		log.Fatal(err)
		return nil, err
	}

	return pipelineCategory, nil
}

type DataLakeDashboardDeserializer struct{}

func NewDataLakeDashboardDeserializer() *DataLakeDashboardDeserializer {
	return &DataLakeDashboardDeserializer{}
}

func (d DataLakeDashboardDeserializer) Unmarshal(data []byte) (interface{}, error) {
	var dashborad data_lake.Dashboard
	dec := json.NewDecoder(strings.NewReader(string(data)))
	dec.DisallowUnknownFields()
	if err := dec.Decode(&dashborad); err != nil && !strings.Contains(err.Error(), "unknown field") {
		log.Println(err)
		return nil, err
	}
	return dashborad, nil
}

type DataLakeDashboardsDeserializer struct{}

func NewDataLakeDashboardsDeserializer() *DataLakeDashboardsDeserializer {
	return &DataLakeDashboardsDeserializer{}
}

func (d DataLakeDashboardsDeserializer) Unmarshal(data []byte) (interface{}, error) {
	var dashborads []data_lake.Dashboard
	dec := json.NewDecoder(strings.NewReader(string(data)))
	dec.DisallowUnknownFields()
	if err := dec.Decode(&dashborads); err != nil && !strings.Contains(err.Error(), "unknown field") {
		log.Println(err)
		return nil, err
	}

	return dashborads, nil
}

type DataLakeWidgetDeserializer struct{}

func NewDataLakeWidgetDeserializer() *DataLakeWidgetDeserializer {
	return &DataLakeWidgetDeserializer{}
}

func (d DataLakeWidgetDeserializer) Unmarshal(data []byte) (interface{}, error) {
	var widget data_lake.DataExplorerWidgetModel
	dec := json.NewDecoder(strings.NewReader(string(data)))
	dec.DisallowUnknownFields()
	if err := dec.Decode(&widget); err != nil && !strings.Contains(err.Error(), "unknown field") {
		log.Println(err)
		return nil, err
	}
	return widget, nil
}

type DataLakeWidgetsDeserializer struct{}

func NewDataLakeWidgetsDeserializer() *DataLakeWidgetsDeserializer {
	return &DataLakeWidgetsDeserializer{}
}

func (d DataLakeWidgetsDeserializer) Unmarshal(data []byte) (interface{}, error) {
	var widgets []data_lake.DataExplorerWidgetModel
	dec := json.NewDecoder(strings.NewReader(string(data)))
	dec.DisallowUnknownFields()
	if err := dec.Decode(&widgets); err != nil && !strings.Contains(err.Error(), "unknown field") {
		log.Println(err)
		return nil, err
	}
	return widgets, nil
}

type SpLogEntriesDeserializer struct{}

func NewSpLogEntriesDeserializer() *SpLogEntriesDeserializer {
	return &SpLogEntriesDeserializer{}
}

func (s SpLogEntriesDeserializer) Unmarshal(data []byte) (interface{}, error) {
	var spLogEntry []functions.SpLogEntry
	dec := json.NewDecoder(strings.NewReader(string(data)))
	dec.DisallowUnknownFields()
	if err := dec.Decode(&spLogEntry); err != nil && !strings.Contains(err.Error(), "unknown field") {
		log.Println(err)
		return nil, err
	}
	return spLogEntry, nil
}

type SpMetricsEntryDeserializer struct{}

func NewSpMetricsEntryDeserializer() *SpMetricsEntryDeserializer {
	return &SpMetricsEntryDeserializer{}
}

func (s SpMetricsEntryDeserializer) Unmarshal(data []byte) (interface{}, error) {
	var spMetricsEntry functions.SpMetricsEntry
	dec := json.NewDecoder(strings.NewReader(string(data)))
	dec.DisallowUnknownFields()
	if err := dec.Decode(&spMetricsEntry); err != nil && !strings.Contains(err.Error(), "unknown field") {
		log.Println(err)
		return nil, err
	}
	return spMetricsEntry, nil
}

type FunctionDefinitionsDeserializer struct{}

func NewFunctionDefinitionsDeserializer() *FunctionDefinitionsDeserializer {
	return &FunctionDefinitionsDeserializer{}
}

func (f FunctionDefinitionsDeserializer) Unmarshal(data []byte) (interface{}, error) {
	var functionDefinitions []functions.FunctionDefinition
	dec := json.NewDecoder(strings.NewReader(string(data)))
	dec.DisallowUnknownFields()
	if err := dec.Decode(&functionDefinitions); err != nil && !strings.Contains(err.Error(), "unknown field") {
		log.Println(err)
		return nil, err
	}
	return functionDefinitions, nil

}

type ShortUserInfosDeserializer struct{}

func NewShortUserInfosDeserializer() *ShortUserInfosDeserializer {
	return &ShortUserInfosDeserializer{}
}

func (s ShortUserInfosDeserializer) Unmarshal(data []byte) (interface{}, error) {
	var shortUserInfo []streampipes_user.ShortUserInfo
	dec := json.NewDecoder(strings.NewReader(string(data)))
	dec.DisallowUnknownFields()
	if err := dec.Decode(&shortUserInfo); err != nil && !strings.Contains(err.Error(), "unknown field") {
		log.Println(err)
		return nil, err
	}
	return shortUserInfo, nil
}

type UserAccountDeserializer struct{}

func NewUserAccountDeserializer() *UserAccountDeserializer {
	return &UserAccountDeserializer{}
}

func (p UserAccountDeserializer) Unmarshal(data []byte) (interface{}, error) {
	var userAccount streampipes_user.UserAccount
	dec := json.NewDecoder(strings.NewReader(string(data)))
	dec.DisallowUnknownFields()
	if err := dec.Decode(&userAccount); err != nil && !strings.Contains(err.Error(), "unknown field") {
		log.Println(err)
		return nil, err
	}
	return userAccount, nil

}

type PipelineDeserializer struct{}

func NewPipelineDeserializer() *PipelineDeserializer {
	return &PipelineDeserializer{}
}

func (p PipelineDeserializer) Unmarshal(data []byte) (interface{}, error) {
	var pipeLine pipeline.Pipeline
	dec := json.NewDecoder(strings.NewReader(string(data)))
	dec.DisallowUnknownFields()
	if err := dec.Decode(&pipeLine); err != nil && !strings.Contains(err.Error(), "unknown field") {
		log.Println(err)
		return nil, err
	}

	return pipeLine, nil

}

type PipelinesDeserializer struct{}

func NewPipelinesDeserializer() *PipelinesDeserializer {
	return &PipelinesDeserializer{}
}

func (p PipelinesDeserializer) Unmarshal(data []byte) (interface{}, error) {
	var pipelines []pipeline.Pipeline

	dec := json.NewDecoder(strings.NewReader(string(data)))
	dec.DisallowUnknownFields()
	if err := dec.Decode(&pipelines); err != nil && !strings.Contains(err.Error(), "unknown field") {
		log.Println(err)
		return nil, err
	}
	return pipelines, nil

}

type PipelineStatusMessagesDeserializer struct{}

func NewPipelineStatusMessagesDeserializer() *PipelineStatusMessagesDeserializer {
	return &PipelineStatusMessagesDeserializer{}
}

func (p PipelineStatusMessagesDeserializer) Unmarshal(data []byte) (interface{}, error) {
	var pipelineStatusMessage []pipeline.PipelineStatusMessage
	dec := json.NewDecoder(strings.NewReader(string(data)))
	dec.DisallowUnknownFields()
	if err := dec.Decode(&pipelineStatusMessage); err != nil && !strings.Contains(err.Error(), "unknown field") {
		log.Println(err)
		return nil, err
	}
	return pipelineStatusMessage, nil
}

type PipelineOperationStatusDeserializer struct{}

func NewPipelineOperationStatusDeserializer() *PipelineOperationStatusDeserializer {
	return &PipelineOperationStatusDeserializer{}
}

func (p PipelineOperationStatusDeserializer) Unmarshal(data []byte) (interface{}, error) {
	var pipelineOperationStatus pipeline.PipelineOperationStatus
	dec := json.NewDecoder(strings.NewReader(string(data)))
	dec.DisallowUnknownFields()
	if err := dec.Decode(&pipelineOperationStatus); err != nil && !strings.Contains(err.Error(), "unknown field") {
		log.Println(err)
		return nil, err
	}
	return pipelineOperationStatus, nil
}

type AdapterDeserializer struct{}

func NewAdapterDeserializer() *AdapterDeserializer {
	return &AdapterDeserializer{}
}

func (a AdapterDeserializer) Unmarshal(data []byte) (interface{}, error) {
	var adapterDescription adapter.AdapterDescription
	dec := json.NewDecoder(strings.NewReader(string(data)))
	dec.DisallowUnknownFields()

	if err := dec.Decode(&adapterDescription); err != nil && !strings.Contains(err.Error(), "unknown field") {
		log.Fatal(err)
		return nil, err
	}
	return adapterDescription, nil

}

type AdaptersDeserializer struct{}

func NewAdaptersDeserializer() *AdaptersDeserializer {
	return &AdaptersDeserializer{}
}

func (a AdaptersDeserializer) Unmarshal(data []byte) (interface{}, error) {
	var adapters []adapter.AdapterDescription

	dec := json.NewDecoder(strings.NewReader(string(data)))
	dec.DisallowUnknownFields()

	if err := dec.Decode(&adapters); err != nil && !strings.Contains(err.Error(), "unknown field") {
		log.Fatal(err)
		return nil, err
	}
	return adapters, nil

}
