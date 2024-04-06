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
	"github.com/apache/streampipes/streampipes-client-go/streampipes/model"
	"github.com/apache/streampipes/streampipes-client-go/streampipes/model/data_lake"
	"github.com/apache/streampipes/streampipes-client-go/streampipes/model/pipeline"
	"github.com/apache/streampipes/streampipes-client-go/streampipes/model/streampipes_version"
)

type Deserializer interface {
	Unmarshal(body []byte) (interface{}, error)
}

var _ Deserializer = (*DataLakeMeasuresDeserializer)(nil)
var _ Deserializer = (*DataSeriesDeserializer)(nil)
var _ Deserializer = (*DataLakeMeasureDeserializer)(nil)

type DataLakeMeasuresDeserializer struct{}

func NewDataLakeMeasuresDeserializer() *DataLakeMeasuresDeserializer {
	return &DataLakeMeasuresDeserializer{}
}

func (d *DataLakeMeasuresDeserializer) Unmarshal(data []byte) (interface{}, error) {
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

func (d *DataLakeMeasureDeserializer) Unmarshal(data []byte) (interface{}, error) {
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

func (d *DataSeriesDeserializer) Unmarshal(data []byte) (interface{}, error) {
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

func (d *StreamPipesVersionDeserializer) Unmarshal(data []byte) (interface{}, error) {
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

func (r *ResponseMessageDeserializer) Unmarshal(data []byte) (interface{}, error) {
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

func (p *PipelineCategoriesDeserializer) Unmarshal(data []byte) (interface{}, error) {
	var pipelineCategory []pipeline.PipelineCategory
	err := json.Unmarshal(data, &pipelineCategory)
	if err != nil {
		return nil, err
	}
	return pipelineCategory, nil
}
