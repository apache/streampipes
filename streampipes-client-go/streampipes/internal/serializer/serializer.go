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
	"streampipes-client-go/streampipes/model/data_lake"
)

type BaseSerializer struct {
	SerializerDataLakeMeasure *data_lake.DataLakeMeasure
}

type BaseSerializerOption func(opts *BaseSerializer)

func NewBaseSerializer(opts ...BaseSerializerOption) *BaseSerializer {
	baseSerializer := BaseSerializer{}
	for _, opt := range opts {
		opt(&baseSerializer)
	}
	return &baseSerializer
}

func WithSerializerDataLakeMeasures() BaseSerializerOption {
	return func(opts *BaseSerializer) {
		opts.SerializerDataLakeMeasure = new(data_lake.DataLakeMeasure)
	}
}

func (b *BaseSerializer) GetMarshal() []byte {
	//Obtain interface type through reflect
	//unrealized
	return nil
}
