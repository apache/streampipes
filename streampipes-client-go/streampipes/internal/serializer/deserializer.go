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
	"log"
	"streampipes-client-go/streampipes/model/data_lake"
)

/*
UnBaseSerializer is used to deserialize the JSON format resource data obtained from the Streampipes API by Go-Client-API
The UnBaseSerializer contains all data models, and in Go Client API,
when using the DataLakeMeasureApi method, the corresponding data models in the UnBaseSerializer should be passed in.
For example, in  "All" method of DataLakeMeasureApi,  "All" method requires obtaining all measurement series,
corresponding to the [] DataLakeMeasure data model, by initializing UnSerializerDataLakeMeasures.
*/

type Deserializer interface {
	GetUnmarshal(body []byte) interface{}
}

var _ Deserializer = (*UnmarshalDataLakeMeasures)(nil)
var _ Deserializer = (*UnmarshalDataSeries)(nil)
var _ Deserializer = (*UnmarshalDataLakeMeasure)(nil)

type UnmarshalDataLakeMeasures struct {
	DeSerializerDataLakeMeasures *[]data_lake.DataLakeMeasure
}

type UnmarshalDataLakeMeasure struct {
	DeSerializerDataLakeMeasure *data_lake.DataLakeMeasure
}

type UnmarshalDataSeries struct {
	DeSerializerDataLakeSeries *data_lake.DataSeries
}

func (d *UnmarshalDataLakeMeasures) GetUnmarshal(data []byte) interface{} {

	err := json.Unmarshal(data, &d.DeSerializerDataLakeMeasures)
	if err != nil {
		log.Fatalf("Serialization failed,because :%v", err)
	}
	return *d.DeSerializerDataLakeMeasures
}

func (d *UnmarshalDataLakeMeasure) GetUnmarshal(data []byte) interface{} {

	err := json.Unmarshal(data, &d.DeSerializerDataLakeMeasure)
	if err != nil {
		log.Fatalf("Serialization failed,because :%v", err)
	}
	return *d.DeSerializerDataLakeMeasure
}

func (d *UnmarshalDataSeries) GetUnmarshal(data []byte) interface{} {
	err := json.Unmarshal(data, &d.DeSerializerDataLakeSeries)

	if err != nil {
		log.Fatalf("Serialization failed,because :%v", err)
	}
	return *d.DeSerializerDataLakeSeries
}
