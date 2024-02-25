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

package streampipes_http

import (
	"github.com/apache/streampipes/streampipes-client-go/streampipes/internal/serializer"
	"github.com/apache/streampipes/streampipes-client-go/streampipes/internal/statu_code"
	"io"
	"log"
	"net/http"
)

type GetRequest struct {
	HttpRequest  *httpRequest
	Deserializer serializer.Deserializer
}

var _ HttpRequest = (*GetRequest)(nil)

func (g *GetRequest) ExecuteRequest(model interface{}) interface{} {
	//Initial Request
	var UnmarshalData interface{}
	g.makeRequest()
	if g.HttpRequest.Response.StatusCode == 200 {
		UnmarshalData = g.afterRequest() // Process Response
	} else {
		switch g.HttpRequest.Response.StatusCode {
		case statu_code.Unauthorized.Code():
			log.Fatal(statu_code.Unauthorized.Code(), statu_code.Unauthorized.Message())
		case statu_code.AccessDenied.Code():
			log.Fatal(statu_code.AccessDenied.Code(), statu_code.AccessDenied.Message())
		case statu_code.MethodNotAllowed.Code():
			log.Fatal(statu_code.MethodNotAllowed.Code(), statu_code.MethodNotAllowed.Message())
		default:
			defer g.HttpRequest.Response.Body.Close()
			body, _ := io.ReadAll(g.HttpRequest.Response.Body)
			log.Fatal(g.HttpRequest.Response.Status, string(body))
		}

	}
	return UnmarshalData
}

func (g *GetRequest) makeRequest() {

	var err error
	g.HttpRequest.Header.Req, _ = http.NewRequest("GET", g.HttpRequest.Url, nil)
	g.HttpRequest.Header.XApiKey(g.HttpRequest.ClientConnectionConfig.Credential.ApiKey)
	g.HttpRequest.Header.XApiUser(g.HttpRequest.ClientConnectionConfig.Credential.UserName)
	g.HttpRequest.Header.AcceptJson()
	g.HttpRequest.Response, err = g.HttpRequest.Client.Do(g.HttpRequest.Header.Req)
	if err != nil {
		log.Fatal(err)
	}
}

func (g *GetRequest) afterRequest() interface{} {

	defer g.HttpRequest.Response.Body.Close()
	body, err := io.ReadAll(g.HttpRequest.Response.Body)
	if err != nil {
		log.Fatal(err)
	}

	UnmarshalData := g.Deserializer.GetUnmarshal(body)
	return UnmarshalData
}

func (g *GetRequest) SetUrl(resourcePath []string) {
	g.HttpRequest.Url = g.HttpRequest.ApiPath.GetBaseUrl(g.HttpRequest.ClientConnectionConfig.Url).AddToPath(resourcePath).ToString()
	log.Print(g.HttpRequest.Url)
}
