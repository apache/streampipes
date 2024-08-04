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

package streampipes

import (
	"bytes"
	"errors"
	"github.com/apache/streampipes/streampipes-client-go/streampipes/config"
	headers "github.com/apache/streampipes/streampipes-client-go/streampipes/internal/http_headers"
	"io"
	"net/http"
)

type endpoint struct {
	config config.StreamPipesClientConfig
}

func (e *endpoint) executeRequest(method string, endPointUrl string, body []byte) (*http.Response, error) {
	var reader io.Reader
	if body != nil {
		reader = bytes.NewReader(body)
	}
	req, err := http.NewRequest(method, endPointUrl, reader)
	if err != nil {
		return nil, err
	}

	header := headers.NewHeaders(req)
	header.SetApiUserAndApiKey(e.config.Credential.UserName, e.config.Credential.ApiKey)
	header.SetAcceptJson()
	header.SetContentTypeJson()

	client := &http.Client{}
	response, err := client.Do(header.GetReq())
	if err != nil {
		return nil, err
	}
	return response, nil
}

func (e *endpoint) handleStatusCode(resp *http.Response) error {

	switch resp.StatusCode {
	case http.StatusUnauthorized:
		return errors.New("401," + "The streamPipes Backend returned an unauthorized error.\nplease check your ApiUser and/or Apikey to be correct.")
	case http.StatusForbidden:
		return errors.New("403," + "There seems to be an issue with the access rights of the given user and the resource you queried.\n" +
			"Apparently, this user is not allowed to query the resource.\n" +
			"Please check the user's permissions or contact your StreamPipes admin.")
	case http.StatusNotFound:
		return errors.New("404," + "There seems to be an issue with the Go Client calling the API inappropriately.\n" +
			"This should not happen, but unfortunately did.\n" +
			"If you don't mind, it would be awesome to let us know by creating an issue at https://github.com/apache/streampipes.\n")
	case http.StatusMethodNotAllowed:
		return errors.New("405," + "There seems to be an issue with the Go Client calling the API inappropriately.\n" +
			"This should not happen, but unfortunately did.\n" +
			"If you don't mind, it would be awesome to let us know by creating an issue at https://github.com/apache/streampipes.\n")
	case http.StatusInternalServerError:
		return errors.New("500," + "streamPipes internal error")
	default:
		return errors.New(resp.Status)
	}

}
