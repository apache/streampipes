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
	"fmt"
	"io"
	"log"
	"net/http"
	"streampipes-client-go/streampipes/internal/serializer"
	"streampipes-client-go/streampipes/internal/statu_code"
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
		case statu_code.BadRequest.Code():
			log.Fatal(statu_code.BadRequest.Code(), statu_code.BadRequest.Message())
		case statu_code.Unauthorized.Code():
			log.Fatal(statu_code.Unauthorized.Code(), statu_code.Unauthorized.Message())
		case statu_code.AccessDenied.Code():
			log.Fatal(statu_code.AccessDenied.Code(), statu_code.AccessDenied.Message())
		case statu_code.MethodNotAllowed.Code():
			log.Fatal(statu_code.MethodNotAllowed.Code(), statu_code.MethodNotAllowed.Message())
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
	d.HttpRequest.Header.XApiUser(d.HttpRequest.ClientConnectionConfig.Credential.UserName)
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
