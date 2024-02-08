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

package config

import (
	"streampipes-client-go/streampipes/internal/credential"
	Path "streampipes-client-go/streampipes/internal/util"
)

type ClientConnectionConfigResolver interface {
	getBaseUrl() *Path.StreamPipesApiPath
	GetConfig() StreamPipesClientConnectionConfig
}

type StreamPipesClientConnectionConfig struct {
	Url        string
	Credential credential.StreamPipesApiKeyCredentials
}

func (s *StreamPipesClientConnectionConfig) GetCredentials() credential.StreamPipesApiKeyCredentials {
	return s.Credential
}

func (s *StreamPipesClientConnectionConfig) GetBaseUrl() *Path.StreamPipesApiPath {

	ApiPath := Path.NewStreamPipesApiPath([]string{s.Url}).FromStreamPipesBasePath()
	ApiPath.ToString()

	return ApiPath
}
