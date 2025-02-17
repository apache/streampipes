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
	"github.com/apache/streampipes/streampipes-client-go/streampipes/model/streampipes_user"
)

type StreamPipesUserInfo struct {
	endpoint
}

func NewStreamPipesUserInfo(clientConfig config.StreamPipesClientConfig) *StreamPipesUserInfo {

	return &StreamPipesUserInfo{
		endpoint{config: clientConfig},
	}
}

func (s *StreamPipesUserInfo) GetSingleStreamPipesUserAccountInfo(principalId string) (streampipes_user.UserAccount, error) {

	endPointUrl := util.NewStreamPipesApiPath(s.config.Url, "streampipes-backend/api/v2/users", []string{principalId})
	log.Printf("Get data from: %s", endPointUrl)

	response, err := s.executeRequest("GET", endPointUrl, nil)
	if err != nil {
		return streampipes_user.UserAccount{}, err
	}

	if response.StatusCode != http.StatusOK {
		err = s.handleStatusCode(response)
		if err != nil {
			return streampipes_user.UserAccount{}, err
		}
	}

	body, err := io.ReadAll(response.Body)
	if err != nil {
		return streampipes_user.UserAccount{}, err
	}

	unmarshalData, err := serializer.NewUserAccountDeserializer().Unmarshal(body)
	if err != nil {
		return streampipes_user.UserAccount{}, err
	}
	userAccount := unmarshalData.(streampipes_user.UserAccount)

	return userAccount, nil
}

func (s *StreamPipesUserInfo) GetAllStreamPipesShortUserInfo() ([]streampipes_user.ShortUserInfo, error) {

	endPointUrl := util.NewStreamPipesApiPath(s.config.Url, "streampipes-backend/api/v2/users", nil)
	log.Printf("Get data from: %s", endPointUrl)

	response, err := s.executeRequest("GET", endPointUrl, nil)
	if err != nil {
		return nil, err
	}

	if response.StatusCode != http.StatusOK {
		err = s.handleStatusCode(response)
		if err != nil {
			return nil, err
		}
	}

	body, err := io.ReadAll(response.Body)
	if err != nil {
		return nil, err
	}

	unmarshalData, err := serializer.NewShortUserInfosDeserializer().Unmarshal(body)
	if err != nil {
		return nil, err
	}
	dataLakeWidget := unmarshalData.([]streampipes_user.ShortUserInfo)

	return dataLakeWidget, nil
}

func (s *StreamPipesUserInfo) DeleteSingleStreamPipesShortUserInfo(principalId string) error {
	endPointUrl := util.NewStreamPipesApiPath(s.config.Url, "streampipes-backend/api/v2/users", []string{principalId})
	log.Printf("Delete data from: %s", endPointUrl)

	response, err := s.executeRequest("DELETE", endPointUrl, nil)
	if err != nil {
		return err
	}

	if response.StatusCode != http.StatusOK {
		err = s.handleStatusCode(response)
		if err != nil {
			return err
		}
	}

	return nil
}
