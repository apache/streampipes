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
	Path "streampipes-client-go/streampipes/internal/streamPipesApiPath"
)

type ClientConnectionConfigResolver interface {
	GetStreamPipesHost() string
	GetStreamPipesPort() string
	IsHttpsDisabled() bool
	GetBaseUrl() string
	GetCredentials() credential.StreamPipesApiKeyCredentials
}

type StreamPipesClientConnectionConfig struct {
	Credential      credential.StreamPipesApiKeyCredentials //credential.StreamPipesApiKeyCredentials
	StreamPipesHost string
	StreamPipesPort string
	HttpsDisabled   bool
}

func (s *StreamPipesClientConnectionConfig) GetCredentials() credential.StreamPipesApiKeyCredentials {
	return s.Credential
}

func (s *StreamPipesClientConnectionConfig) GetStreamPipesHost() string {
	return s.StreamPipesHost
}

func (s *StreamPipesClientConnectionConfig) GetStreamPipesPort() string {
	return s.StreamPipesPort
}

func (s *StreamPipesClientConnectionConfig) IsHttpsDisabled() bool {
	return s.HttpsDisabled
}

func (s *StreamPipesClientConnectionConfig) GetBaseUrl() *Path.StreamPipesApiPath {
	protocol := "https://"
	if s.IsHttpsDisabled() {
		protocol = "http://"
	}

	protocol = protocol + s.StreamPipesHost + ":" + s.StreamPipesPort
	ApiPath := Path.NewStreamPipesApiPath([]string{protocol}).FromStreamPipesBasePath()
	ApiPath.ToString()

	return ApiPath
}
