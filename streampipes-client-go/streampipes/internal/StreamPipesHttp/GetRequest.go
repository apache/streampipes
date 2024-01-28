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
	"io/ioutil"
	"log"
	"streampipes-client-go/streampipes/internal/config"
	"streampipes-client-go/streampipes/internal/serializer"
)

type GetRequest struct {
	HttpRequest *HttpRequest
	Serializer  *serializer.UnBaseSerializer
}

func NewGetRequest(clientConfig config.StreamPipesClientConnectionConfig, Serializer *serializer.UnBaseSerializer) *GetRequest {
	return &GetRequest{
		HttpRequest: NewHttpRequest(clientConfig),
		Serializer:  Serializer,
	}
}

func (g *GetRequest) ExecuteGetRequest(Type interface{}) {
	//Process complete GET requests
	g.HttpRequest.ExecuteRequest("GET", Type)
	g.HttpRequest.AfterRequest = func(Type interface{}) {
		g.afterRequest(Type)
	}
	g.HttpRequest.AfterRequest(Type)
}

func (g *GetRequest) afterRequest(Type interface{}) {
	////Process complete GET requests
	defer g.HttpRequest.Response.Body.Close()
	body, err := ioutil.ReadAll(g.HttpRequest.Response.Body)
	if err != nil {
		log.Fatal(err)
	}
	err = g.Serializer.GetUnmarshal(body, Type)
	if err != nil {
		log.Fatal("Serialization failed")
	}
}
