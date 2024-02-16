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
	"reflect"
	"streampipes-client-go/streampipes/model/data_lake"
)

/*
UnBaseSerializer is used to deserialize the JSON format resource data obtained from the Streampipes API by Go-Client-API
The UnBaseSerializer contains all data models, and in Go Client API,
when using the DataLakeMeasureApi method, the corresponding data models in the UnBaseSerializer should be passed in.
For example, in  "All" method of DataLakeMeasureApi,  "All" method requires obtaining all measurement series,
corresponding to the [] DataLakeMeasure data model, by initializing UnSerializerDataLakeMeasures.
*/
//It is not allowed to initialize the remaining data models.

type UnBaseSerializer struct {
	UnSerializerDataLakeMeasures *[]data_lake.DataLakeMeasure
	UnSerializerDataLakeSeries   *data_lake.DataSeries
}

type UnBaseSerializerOption func(opts *UnBaseSerializer)

func NewBaseUnSerializer(opts ...UnBaseSerializerOption) *UnBaseSerializer {
	unBaseSerializer := UnBaseSerializer{}
	for _, opt := range opts {
		opt(&unBaseSerializer)
	}
	return &unBaseSerializer
}

func WithUnSerializerDataLakeMeasures() UnBaseSerializerOption {

	return func(opts *UnBaseSerializer) {
		opts.UnSerializerDataLakeMeasures = new([]data_lake.DataLakeMeasure)
	}
}

func WithUnSerializerDataSeries() UnBaseSerializerOption {
	return func(opts *UnBaseSerializer) {
		opts.UnSerializerDataLakeSeries = new(data_lake.DataSeries)
	}
}

func (u *UnBaseSerializer) GetUnmarshal(body []byte) error {
	//By using the Go reflection method, dynamically obtain the instantiated fields with in UnBaseSerializer for deserialization

	v := reflect.ValueOf(u).Elem()
	for i := 0; i < v.NumField(); i++ {
		field := v.Field(i)
		if !field.IsZero() {
			err := json.Unmarshal(body, field.Addr().Interface())
			if err != nil {
				return err
			}
		}
	}
	return nil
}
