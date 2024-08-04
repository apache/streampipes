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
	"io"
	"log"
	"net/http"

	"github.com/apache/streampipes/streampipes-client-go/streampipes/config"
	"github.com/apache/streampipes/streampipes-client-go/streampipes/internal/serializer"
	"github.com/apache/streampipes/streampipes-client-go/streampipes/internal/util"
	"github.com/apache/streampipes/streampipes-client-go/streampipes/model/streampipes_version"
)

type Versions struct {
	endpoint
}

func NewVersions(clientConfig config.StreamPipesClientConfig) *Versions {

	return &Versions{
		endpoint{config: clientConfig},
	}
}

// GetStreamPipesVersion provides health-check and information about current backend version.
func (d *Versions) GetStreamPipesVersion() (streampipes_version.Versions, error) {

	endPointUrl := util.NewStreamPipesApiPath(d.config.Url, "streampipes-backend/api/v2/info/versions", nil)
	log.Printf("Get data from: %s", endPointUrl)

	response, err := d.executeRequest("GET", endPointUrl, nil)
	if err != nil {
		return streampipes_version.Versions{}, err
	}

	if response.StatusCode != http.StatusOK {
		err = d.handleStatusCode(response)
		if err != nil {
			return streampipes_version.Versions{}, err
		}
	}

	body, err := io.ReadAll(response.Body)
	if err != nil {
		return streampipes_version.Versions{}, err
	}

	unmarshalData, err := serializer.NewStreamPipesVersionDeserializer().Unmarshal(body)
	if err != nil {
		return streampipes_version.Versions{}, err
	}
	version := unmarshalData.(streampipes_version.Versions)
	return version, nil
}
