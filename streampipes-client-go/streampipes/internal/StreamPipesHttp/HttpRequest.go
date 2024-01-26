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
	"log"
	"net/http"
	"streampipes-client-go/streampipes/internal/StatuCode"
	"streampipes-client-go/streampipes/internal/StreamPipesHttp/headers"
	"streampipes-client-go/streampipes/internal/config"
	Path "streampipes-client-go/streampipes/internal/streamPipesApiPath"
	"streampipes-client-go/streampipes/model/resource"
)

type AfterRequestFunc func(dataLakeMeasure *[]resource.DataLakeMeasure)

type HttpRequest struct {
	ClientConnectionConfig config.StreamPipesClientConnectionConfig
	ApiPath                *Path.StreamPipesApiPath
	Header                 *headers.Headers
	Client                 *http.Client
	Response               *http.Response
	AfterRequest           AfterRequestFunc
	Url                    string
}

func NewHttpRequest(clientConfig config.StreamPipesClientConnectionConfig) *HttpRequest {
	afterRequest := func(dataLakeMeasure *[]resource.DataLakeMeasure) {}
	return &HttpRequest{
		ClientConnectionConfig: clientConfig,
		Header:                 new(headers.Headers),
		Client:                 new(http.Client),
		Response:               new(http.Response),
		AfterRequest:           afterRequest,
		Url:                    "",
	}
}

// Build a complete URL
func (r *HttpRequest) MakeUrl(resourcePath []string) {

	r.Url = r.ClientConnectionConfig.GetBaseUrl().AddToPath(resourcePath).ToString()

}

// Complete request process
func (r *HttpRequest) ExecuteRequest(method string, dataLakeMeasure *[]resource.DataLakeMeasure) {
	//Initial Request
	r.makeRequest(method)

	//str := strconv.Itoa(r.Response.StatusCode)
	//if str[:2] == "20" {
	//	r.AfterRequest(dataLakeMeasure)
	//} else {
	if r.Response.StatusCode == 200 {
		r.AfterRequest(dataLakeMeasure) // Process Response
	} else {
		switch r.Response.StatusCode {
		case StatuCode.Unauthorized.Code():
			log.Fatal(StatuCode.Unauthorized.Code(), StatuCode.Unauthorized.Message())
		case StatuCode.AccessDenied.Code():
			log.Fatal(StatuCode.AccessDenied.Code(), StatuCode.AccessDenied.Message())
		case StatuCode.NotFound.Code():
			log.Fatal(StatuCode.NotFound.Code(), StatuCode.NotFound.Message())
		case StatuCode.MethodNotAllowed.Code():
			log.Fatal(StatuCode.MethodNotAllowed.Code(), StatuCode.MethodNotAllowed.Message())
		default:
			log.Fatal(r.Response.StatusCode)
		}

	}
}

// Initial Request
func (r *HttpRequest) makeRequest(method string) {
	var err error
	r.Header.Req, _ = http.NewRequest(method, r.Url, nil)
	r.Header.XApiKey(r.ClientConnectionConfig.Credential.ApiKey)
	r.Header.XApiUser(r.ClientConnectionConfig.Credential.Username)
	r.Header.AcceptJson()
	r.Response, err = r.Client.Do(r.Header.Req)
	//todo

	if err != nil {
		log.Fatal(err)
	}
}

//func (r *HttpRequest) StandardJsonHeaders() []StreamPipesHttp.Header {
//	//r.Header.XApiKey(r.ClientConnectionConfig.Credential.ApiKey)
//	//r.Header.XApiUser(r.ClientConnectionConfig.Credential.Username)
//	//r.Header.AcceptJson()
//	headers := r.ClientConnectionConfig.GetCredentials().MakeHeaders()
//}
//
//func (r *HttpRequest) StandardHeaders() []*Header {
//	headers := make([]*Header, 0)
//	credentialsHeaders := r.ConnectionConfig.GetCredentials().MakeHeaders()
//	headers = append(headers, credentialsHeaders...)
//	return headers
//}

//func (r *HttpRequest) StandardPostHeaders() []*Header {
//	headers := make([]*Header, 0)
//	jsonHeaders := r.StandardJsonHeaders()
//	headers = append(headers, jsonHeaders...)
//	headers = append(headers, &Header{Name: "Content-Type", Value: "application/json"})
//	return headers
//}

//func (r *HttpRequest) WriteToFile(fileLocation string) {
//	urlString, _ := r.MakeUrl()
//	url, _ := URL.Parse(urlString)
//
//	connection, _ := url.OpenConnection()
//	credentialsHeader := r.ConnectionConfig.GetCredentials().MakeHeaders()[0]
//	connection.SetRequestProperty("Authorization", credentialsHeader.Elements[0].Name)
//
//	readableByteChannel := connection.GetInputStream()
//	fileOutputStream, _ := FileOutputStream(fileLocation)
//	fileOutputStream.GetChannel().TransferFrom(readableByteChannel, 0, int64(^uint(0)>>1))
//}
//
//func (r *HttpRequest) EntityAsString(entity HttpEntity) string {
//	return EntityUtils.ToString(entity)
//}
//
//func (r *HttpRequest) EntityAsByteArray(entity HttpEntity) []byte {
//	return EntityUtils.ToByteArray(entity)
//}
