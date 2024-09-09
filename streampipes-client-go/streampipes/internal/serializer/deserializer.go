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
	err := json.Unmarshal(data, &dataLakeMeasures)
	if err != nil {
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
	err := json.Unmarshal(data, &dataLakeMeasure)
	if err != nil {
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
	err := json.Unmarshal(data, &dataSeries)
	if err != nil {
		return nil, err
	}
	return dataSeries, nil
}

type StreamPipesVersionDeserializer struct{}

func NewStreamPipesVersionDeserializer() *StreamPipesVersionDeserializer {
	return &StreamPipesVersionDeserializer{}
}

func (d StreamPipesVersionDeserializer) Unmarshal(data []byte) (interface{}, error) {
	var dataSeries streampipes_version.Versions
	err := json.Unmarshal(data, &dataSeries)
	if err != nil {
		return nil, err
	}
	return dataSeries, nil
}

type ResponseMessageDeserializer struct{}

func NewResponseMessageDeserializer() *ResponseMessageDeserializer {
	return &ResponseMessageDeserializer{}
}

func (r ResponseMessageDeserializer) Unmarshal(data []byte) (interface{}, error) {
	var responseMessage model.ResponseMessage
	err := json.Unmarshal(data, &responseMessage)
	if err != nil {
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
	err := json.Unmarshal(data, &pipelineCategory)
	if err != nil {
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
	err := json.Unmarshal(data, &dashborad)
	if err != nil {
		return nil, err
	}
	return dashborad, nil
}

type DataLakeDashboardsDeserializer struct{}

func NewDataLakeDashboardsDeserializer() *DataLakeDashboardsDeserializer {
	return &DataLakeDashboardsDeserializer{}
}

func (d DataLakeDashboardsDeserializer) Unmarshal(data []byte) (interface{}, error) {
	var dashborad []data_lake.Dashboard
	err := json.Unmarshal(data, &dashborad)
	if err != nil {
		return nil, err
	}

	return dashborad, nil
}

type DataLakeWidgetDeserializer struct{}

func NewDataLakeWidgetDeserializer() *DataLakeWidgetDeserializer {
	return &DataLakeWidgetDeserializer{}
}

func (d DataLakeWidgetDeserializer) Unmarshal(data []byte) (interface{}, error) {
	var widget data_lake.DataExplorerWidgetModel
	err := json.Unmarshal(data, &widget)
	if err != nil {
		return nil, err
	}
	return widget, nil
}

type DataLakeWidgetsDeserializer struct{}

func NewDataLakeWidgetsDeserializer() *DataLakeWidgetsDeserializer {
	return &DataLakeWidgetsDeserializer{}
}

func (d DataLakeWidgetsDeserializer) Unmarshal(data []byte) (interface{}, error) {
	var widget []data_lake.DataExplorerWidgetModel
	err := json.Unmarshal(data, &widget)
	if err != nil {
		return nil, err
	}
	return widget, nil
}

type SpLogEntriesDeserializer struct{}

func NewSpLogEntriesDeserializer() *SpLogEntriesDeserializer {
	return &SpLogEntriesDeserializer{}
}

func (p SpLogEntriesDeserializer) Unmarshal(data []byte) (interface{}, error) {
	var userAccount []functions.SpLogEntry
	err := json.Unmarshal(data, &userAccount)
	if err != nil {
		return nil, err
	}
	return userAccount, nil
}

type SpMetricsEntryDeserializer struct{}

func NewSpMetricsEntryDeserializer() *SpMetricsEntryDeserializer {
	return &SpMetricsEntryDeserializer{}
}

func (p SpMetricsEntryDeserializer) Unmarshal(data []byte) (interface{}, error) {
	var spMetricsEntry functions.SpMetricsEntry
	err := json.Unmarshal(data, &spMetricsEntry)
	if err != nil {
		return nil, err
	}
	return spMetricsEntry, nil
}

type FunctionDefinitionsDeserializer struct{}

func NewFunctionDefinitionsDeserializer() *FunctionDefinitionsDeserializer {
	return &FunctionDefinitionsDeserializer{}
}

func (p FunctionDefinitionsDeserializer) Unmarshal(data []byte) (interface{}, error) {
	var functionDefinitions []functions.FunctionDefinition
	err := json.Unmarshal(data, &functionDefinitions)
	if err != nil {
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
	err := json.Unmarshal(data, &shortUserInfo)
	if err != nil {
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
	err := json.Unmarshal(data, &userAccount)
	if err != nil {
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
	err := json.Unmarshal(data, &pipeLine)
	if err != nil {
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
	err := json.Unmarshal(data, &pipelines)
	if err != nil {
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
	err := json.Unmarshal(data, &pipelineStatusMessage)
	if err != nil {
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
	err := json.Unmarshal(data, &pipelineOperationStatus)
	if err != nil {
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
	err := json.Unmarshal(data, &adapterDescription)
	if err != nil {
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
	err := json.Unmarshal(data, &adapters)
	if err != nil {
		return nil, err
	}
	return adapters, nil

}
