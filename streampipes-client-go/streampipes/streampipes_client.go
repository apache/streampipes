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
	"errors"
	"net/url"
	"strings"

	"github.com/apache/streampipes/streampipes-client-go/streampipes/config"
	"github.com/apache/streampipes/streampipes-client-go/streampipes/utils"
)

// This is the central point of contact with StreamPipes and provides all the functionalities to interact with it.
// The client provides so-called "API", each of which refers to the endpoint of the StreamPipes API.
// e.g. `DataLakeMeasure` provides the actual methods to interact with StreamPipes API.

type StreamPipesClient struct {
	config config.StreamPipesClientConfig
}

// NewStreamPipesClient returns an instance of *StreamPipesClient
// Currently, it does not support HTTPS connections or connections to port 443.
func NewStreamPipesClient(c config.StreamPipesClientConfig) (*StreamPipesClient, error) {

	if c.Credential == (config.StreamPipesApiKeyCredentials{}) {
		return nil, errors.New("no credential entered")
	}

	if !utils.CheckUrl(c.Url) {
		return nil, errors.New("please check if the URL is correct,Must be in the form of A://B:C," +
			"where A is either HTTP, not case sensitive. B must be the host and C must be the port")
	}

	Url, err := url.Parse(c.Url)
	if err != nil {
		return nil, err
	}

	if strings.EqualFold(Url.Scheme, "https") || Url.Port() == "443" {
		return nil, errors.New(
			"The URL passed in is invalid and does not support HTTPS or port 443.\n ")
	}

	return &StreamPipesClient{
		c,
	}, nil

}

func (s *StreamPipesClient) DataLakeMeasures() *DataLakeMeasure {

	return NewDataLakeMeasures(s.config)
}

func (s *StreamPipesClient) StreamPipesVersion() *Versions {

	return NewVersions(s.config)
}

func (s *StreamPipesClient) Pipeline() *Pipeline {

	return NewPipeline(s.config)
}

func (s *StreamPipesClient) Adapter() *Adapter {
	return NewAdapter(s.config)
}

func (s *StreamPipesClient) DataLakeDashboard() *DataLakeDashboard {

	return NewDataLakeDashborad(s.config)
}

func (s *StreamPipesClient) DataLakeWidget() *DataLakeWidget {

	return NewDataLakeWidget(s.config)
}

func (s *StreamPipesClient) Function() *Functions {

	return NewFunctions(s.config)

}

func (s *StreamPipesClient) UserInfo() *StreamPipesUserInfo {

	return NewStreamPipesUserInfo(s.config)

}
