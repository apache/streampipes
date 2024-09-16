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
	"fmt"
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
		return fmt.Errorf("response code %d:"+"The streamPipes Backend returned an unauthorized error.\nplease check your ApiUser and/or Apikey to be correct.", resp.StatusCode)
	case http.StatusForbidden:
		return fmt.Errorf("response code %d:"+"There seems to be an issue with the access rights of the given user and the resource you queried.\n"+
			"Apparently, this user is not allowed to query the resource.\n"+
			"Please check the user's permissions or contact your StreamPipes admin.", resp.StatusCode)
	case http.StatusNotFound:
		return fmt.Errorf("response code %d:"+"There seems to be an issue with the Go Client calling the API inappropriately.\n"+
			"This should not happen, but unfortunately did.\n"+
			"If you don't mind, it would be awesome to let us know by creating an issue at https://github.com/apache/streampipes.\n", resp.StatusCode)
	case http.StatusMethodNotAllowed:
		return fmt.Errorf("response code %d:"+"There seems to be an issue with the Go Client calling the API inappropriately.\n"+
			"This should not happen, but unfortunately did.\n"+
			"If you don't mind, it would be awesome to let us know by creating an issue at https://github.com/apache/streampipes.\n", resp.StatusCode)
	case http.StatusInternalServerError:
		return fmt.Errorf("response code %d:"+"streamPipes internal error", resp.StatusCode)
	default:
		return fmt.Errorf(resp.Status)
	}

}
