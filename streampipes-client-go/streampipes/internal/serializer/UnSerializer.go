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
	"streampipes-client-go/streampipes/model/DataLake"
)

type UnBaseSerializer struct {
	UnSerializerDataLakeMeasures *[]DataLake.DataLakeMeasure
	UnSerializerDataLakeSeries   *DataLake.DataSeries
}

type Option func(opts *UnBaseSerializer)

func NewBaseUnSerializer(opts ...Option) *UnBaseSerializer {
	unBaseSerializer := UnBaseSerializer{}
	for _, opt := range opts {
		opt(&unBaseSerializer)
	}
	return &unBaseSerializer
}

func WithUnSerializerDataLakeMeasures(DataLakeMeasures *[]DataLake.DataLakeMeasure) Option {
	return func(opts *UnBaseSerializer) {
		opts.UnSerializerDataLakeMeasures = DataLakeMeasures
	}
}

func WithUnSerializerDataSeries(DataSeries *DataLake.DataSeries) Option {
	return func(opts *UnBaseSerializer) {
		opts.UnSerializerDataLakeSeries = DataSeries
	}
}

func (u *UnBaseSerializer) GetUnmarshal(body []byte, Interface interface{}) error {
	//Obtain interface type through reflect
	Type := reflect.TypeOf(Interface)
	Value := reflect.ValueOf(u).Elem()
	for i := 0; i < Value.NumField(); i++ {
		field := Value.Field(i)
		if field.Type() == Type {
			err := json.Unmarshal(body, field.Interface())
			if err != nil {
				return err
			}
		}
	}
	return nil
}
