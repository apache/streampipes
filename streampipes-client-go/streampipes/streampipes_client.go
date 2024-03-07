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
	"github.com/apache/streampipes/streampipes-client-go/streampipes/config"
	"github.com/apache/streampipes/streampipes-client-go/streampipes/utils"
	"log"
	"net/url"
	"strings"
)

/*
 This is the central point of contact with StreamPipes and provides all the functionalities to interact with it.
 The client provides so-called "API", each of which refers to the endpoint of the StreamPipes API.
 e.g. `DataLakeMeasure` provides the actual methods to interact with StreamPipes API.
*/

type StreamPipesClient struct {
	Config config.StreamPipesClientConnectConfig
}

func NewStreamPipesClient(Config config.StreamPipesClientConnectConfig) (*StreamPipesClient, error) {

	//NewStreamPipesClient returns an instance of * StreamPipesClient
	//Temporarily does not support HTTPS connections, nor does it support connecting to port 443

	if Config.Credential == (config.StreamPipesApiKeyCredentials{}) {
		log.Fatal("No credential entered")
	}

	if !utils.CheckUrl(Config.Url) {
		log.Fatal("Please check if the URL is correct,Must be in the form of A://B:C," +
			"where A is either HTTP or HTTPS, not case sensitive.B must be the host and C must be the port.")
	}

	Url, err := url.Parse(Config.Url)
	if err != nil {
		log.Fatal("Please enter the correct URL", err)
	}

	if strings.EqualFold(Url.Scheme, "https") || Url.Port() == "443" {
		return &StreamPipesClient{}, errors.New(
			"Invalid configuration passed! The given client configuration has " +
				"`https` and `port` set to `443`.\n ")
	}

	return &StreamPipesClient{
		Config,
	}, nil

}

func (s *StreamPipesClient) DataLakeMeasures() *DataLakeMeasure {

	return NewDataLakeMeasures(s.Config)

}
