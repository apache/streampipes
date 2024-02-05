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

package StreamPipesHttp

import (
	"net/http"
	"streampipes-client-go/streampipes/config"
	"streampipes-client-go/streampipes/internal/StreamPipesHttp/headers"
	Path "streampipes-client-go/streampipes/internal/util"
)

type HttpRequest interface {
	ExecuteRequest(serializerStruct interface{}) interface{}
	MakeUrl(resourcePath []string)
}

type httpRequest struct {
	ClientConnectionConfig config.StreamPipesClientConnectionConfig
	ApiPath                *Path.StreamPipesApiPath
	Header                 *headers.Headers
	Client                 *http.Client
	Response               *http.Response
	Url                    string
}

func NewHttpRequest(clientConfig config.StreamPipesClientConnectionConfig) *httpRequest {
	return &httpRequest{
		ClientConnectionConfig: clientConfig,
		Header:                 new(headers.Headers),
		Client:                 new(http.Client),
		Response:               new(http.Response),
		Url:                    "",
	}
}
