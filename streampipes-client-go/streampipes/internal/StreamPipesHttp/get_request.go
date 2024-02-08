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
	"io"
	"log"
	"net/http"
	"streampipes-client-go/streampipes/internal/StatuCode"
	"streampipes-client-go/streampipes/internal/serializer"
)

type GetRequest struct {
	HttpRequest  *httpRequest
	UnSerializer *serializer.UnBaseSerializer
}

var _ HttpRequest = (*GetRequest)(nil)

func (g *GetRequest) ExecuteRequest(serializerStruct interface{}) interface{} {
	//Initial Request
	g.makeRequest()
	if g.HttpRequest.Response.StatusCode == 200 {
		g.afterRequest() // Process Response
	} else {
		switch g.HttpRequest.Response.StatusCode {
		case StatuCode.Unauthorized.Code():
			log.Fatal(StatuCode.Unauthorized.Code(), StatuCode.Unauthorized.Message())
		case StatuCode.AccessDenied.Code():
			log.Fatal(StatuCode.AccessDenied.Code(), StatuCode.AccessDenied.Message())
		case StatuCode.MethodNotAllowed.Code():
			log.Fatal(StatuCode.MethodNotAllowed.Code(), StatuCode.MethodNotAllowed.Message())
		default:
			defer g.HttpRequest.Response.Body.Close()
			body, _ := io.ReadAll(g.HttpRequest.Response.Body)
			log.Fatal(g.HttpRequest.Response.Status, string(body))
		}

	}
	return g.UnSerializer
}

func (g *GetRequest) afterRequest() {
	//Process complete GET requests
	defer g.HttpRequest.Response.Body.Close()
	body, err := io.ReadAll(g.HttpRequest.Response.Body)
	if err != nil {
		log.Fatal(err)
	}
	err = g.UnSerializer.GetUnmarshal(body)
	if err != nil {
		log.Fatal("Serialization failed")
	}
}

func (g *GetRequest) makeRequest() {
	var err error
	g.HttpRequest.Header.Req, _ = http.NewRequest("GET", g.HttpRequest.Url, nil)
	g.HttpRequest.Header.XApiKey(g.HttpRequest.ClientConnectionConfig.Credential.ApiKey)
	g.HttpRequest.Header.XApiUser(g.HttpRequest.ClientConnectionConfig.Credential.Username)
	g.HttpRequest.Header.AcceptJson()
	g.HttpRequest.Response, err = g.HttpRequest.Client.Do(g.HttpRequest.Header.Req)
	if err != nil {
		log.Fatal(err)
	}
}

func (g *GetRequest) MakeUrl(resourcePath []string) {

	g.HttpRequest.Url = g.HttpRequest.ClientConnectionConfig.GetBaseUrl().AddToPath(resourcePath).ToString()
}
