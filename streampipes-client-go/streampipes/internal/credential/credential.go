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

package credential

import (
	"net/http"
	"streampipes-client-go/streampipes/internal/StreamPipesHttp/headers"
)

//type CredentialsProvider interface {
//	MakeHeaders() []StreamPipesHttp.Header
//}

type StreamPipesApiKeyCredentials struct {
	Username string
	ApiKey   string
}

func (s *StreamPipesApiKeyCredentials) ApiKeyCredential(username, apikey string) {
	s.Username = username
	s.ApiKey = apikey
}

func (s *StreamPipesApiKeyCredentials) GetUsername() string {
	return s.Username
}
func (s *StreamPipesApiKeyCredentials) GetApiKey() string {
	return s.ApiKey
}

func (s *StreamPipesApiKeyCredentials) MakeHeaders(header *headers.Headers) (Rheader []http.Header) {
	XApiUser := header.XApiUser(s.Username)
	XApiKey := header.XApiKey(s.ApiKey)
	Rheader = append(append(Rheader, XApiUser), XApiKey)
	return Rheader
}
