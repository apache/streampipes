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

package http_headers

import (
	"net/http"
)

type Headers struct {
	req    *http.Request
	header http.Header
}

func NewHeaders(req *http.Request) *Headers {
	return &Headers{
		req:    req,
		header: nil,
	}
}

func (h *Headers) SetApiUserAndApiKey(apiUser string, apiKey string) {
	h.header = h.req.Header
	h.header.Set("X-API-USER", apiUser)
	h.header.Set("X-API-KEY", apiKey)
}

func (h *Headers) SetAcceptJson() {
	h.header = h.req.Header
	h.header.Set("Accept", "application/json")
}

func (h *Headers) SetContentTypeJson() {
	h.header = h.req.Header
	h.header.Set("Content-type", "application/json")
}

func (h *Headers) GetReq() *http.Request {
	return h.req
}
