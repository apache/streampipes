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

package headers

import (
	"net/http"
)

type Headers struct {
	Req    *http.Request
	Header http.Header
}

func (h *Headers) AuthorizationBearer(bearerToken string) http.Header {
	h.Header = h.Req.Header
	h.Header.Set("Authorization", "Bearer "+bearerToken)
	return h.Header
}

func (h *Headers) XApiKey(apiKey string) http.Header {
	h.Header = h.Req.Header
	h.Header.Set("X-API-KEY", apiKey)
	return h.Header
}

func (h *Headers) XApiUser(apiUser string) http.Header {
	h.Header = h.Req.Header
	h.Header.Set("X-API-USER", apiUser)
	return h.Header
}

func (h *Headers) AcceptJson() http.Header {
	h.Header = h.Req.Header
	h.Header.Set("Accept", "application/json")
	return h.Header
}

func (h *Headers) ContentTypeJson() http.Header {
	h.Header = h.Req.Header
	h.Header.Set("Content-type", "application/json")
	return h.Header
}
