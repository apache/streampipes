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
	"github.com/apache/streampipes/streampipes-client-go/streampipes/model/pipeline"
)

type PipelineSerializer struct{}

func NewPipelineSerializer() PipelineSerializer {
	return PipelineSerializer{}
}

func (p PipelineSerializer) Marshal(pp pipeline.Pipeline) ([]byte, error) {
	data, err := json.Marshal(pp)
	if err != nil {
		return nil, err
	}
	return data, nil
}

type AdapterSerializer struct{}

func NewAdapterSerializer() AdapterSerializer {
	return AdapterSerializer{}
}

func (a AdapterSerializer) Marshal(adapters adapter.AdapterDescription) ([]byte, error) {
	data, err := json.Marshal(adapters)
	if err != nil {
		return nil, err
	}
	return data, nil
}
