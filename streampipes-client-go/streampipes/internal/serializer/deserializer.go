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
	"github.com/apache/streampipes/streampipes-client-go/streampipes/model/data_lake"
)

type Deserializer interface {
	Unmarshal(body []byte) (interface{}, error)
}

var _ Deserializer = (*DataLakeMeasuresDeserializer)(nil)
var _ Deserializer = (*DataSeriesDeSerializer)(nil)
var _ Deserializer = (*DataLakeMeasureDeSerializer)(nil)

type DataLakeMeasuresDeserializer struct{}

func NewUnmarshalDataLakeMeasures() *DataLakeMeasuresDeserializer {
	return &DataLakeMeasuresDeserializer{}
}

func (u *DataLakeMeasuresDeserializer) Unmarshal(data []byte) (interface{}, error) {
	var DeSerializerDataLakeMeasures []data_lake.DataLakeMeasure
	err := json.Unmarshal(data, &DeSerializerDataLakeMeasures)
	if err != nil {
		return nil, err
	}
	return DeSerializerDataLakeMeasures, nil
}

type DataLakeMeasureDeSerializer struct{}

func NewUnmarshalDataLakeMeasure() *DataLakeMeasureDeSerializer {
	return &DataLakeMeasureDeSerializer{}
}

func (u *DataLakeMeasureDeSerializer) Unmarshal(data []byte) (interface{}, error) {
	var DeSerializerDataLakeMeasure data_lake.DataLakeMeasure
	err := json.Unmarshal(data, &DeSerializerDataLakeMeasure)
	if err != nil {
		return nil, err
	}
	return DeSerializerDataLakeMeasure, nil
}

type DataSeriesDeSerializer struct{}

func NewUnmarshalDataSeries() *DataSeriesDeSerializer {
	return &DataSeriesDeSerializer{}
}

func (u *DataSeriesDeSerializer) Unmarshal(data []byte) (interface{}, error) {
	var DeSerializerDataLakeSeries data_lake.DataSeries
	err := json.Unmarshal(data, &DeSerializerDataLakeSeries)
	if err != nil {
		return nil, err
	}
	return DeSerializerDataLakeSeries, nil
}
