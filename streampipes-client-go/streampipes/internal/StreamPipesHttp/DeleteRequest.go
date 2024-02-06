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
	"fmt"
	"io"
	"log"
	"net/http"
	"streampipes-client-go/streampipes/internal/StatuCode"
	"streampipes-client-go/streampipes/internal/serializer"
)

type DeleteRequest struct {
	HttpRequest  *httpRequest
	UnSerializer *serializer.UnBaseSerializer
}

var _ HttpRequest = (*DeleteRequest)(nil)

func (d *DeleteRequest) ExecuteRequest(serializerStruct interface{}) interface{} {
	//Initial Request
	d.makeRequest()
	if d.HttpRequest.Response.StatusCode == 200 {
		return "OK"
	} else {
		switch d.HttpRequest.Response.StatusCode {
		case StatuCode.BadRequest.Code():
			log.Fatal(StatuCode.BadRequest.Code(), StatuCode.BadRequest.Message())
		case StatuCode.Unauthorized.Code():
			log.Fatal(StatuCode.Unauthorized.Code(), StatuCode.Unauthorized.Message())
		case StatuCode.AccessDenied.Code():
			log.Fatal(StatuCode.AccessDenied.Code(), StatuCode.AccessDenied.Message())
		case StatuCode.MethodNotAllowed.Code():
			log.Fatal(StatuCode.MethodNotAllowed.Code(), StatuCode.MethodNotAllowed.Message())
		default:
			defer d.HttpRequest.Response.Body.Close()
			body, _ := io.ReadAll(d.HttpRequest.Response.Body)
			log.Fatal(d.HttpRequest.Response.Status, string(body))
		}

	}
	return nil
}

func (d *DeleteRequest) makeRequest() {
	var err error
	d.HttpRequest.Header.Req, _ = http.NewRequest("DELETE", d.HttpRequest.Url, nil)
	d.HttpRequest.Header.XApiKey(d.HttpRequest.ClientConnectionConfig.Credential.ApiKey)
	d.HttpRequest.Header.XApiUser(d.HttpRequest.ClientConnectionConfig.Credential.Username)
	d.HttpRequest.Header.AcceptJson()
	d.HttpRequest.Response, err = d.HttpRequest.Client.Do(d.HttpRequest.Header.Req)
	if err != nil {
		log.Fatal(err)
	}
}

func (d *DeleteRequest) MakeUrl(resourcePath []string) {

	d.HttpRequest.Url = d.HttpRequest.ClientConnectionConfig.GetBaseUrl().AddToPath(resourcePath).ToString()
	fmt.Println(d.HttpRequest.Url)
}
