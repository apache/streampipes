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

package streampipes_user

type ShortUserInfo struct {
	PrincipalID string `json:"principalId"`
	Email       string `json:"email"`
	DisplayName string `json:"displayName"`
}

type UserAccount struct {
	PrincipalID             string         `json:"principalId"`
	Rev                     string         `json:"rev"`
	Username                string         `json:"username"`
	ObjectPermissions       []string       `json:"objectPermissions"`
	Roles                   []string       `json:"roles"`
	Groups                  []string       `json:"groups"`
	AccountEnabled          bool           `json:"accountEnabled"`
	AccountLocked           bool           `json:"accountLocked"`
	AccountExpired          bool           `json:"accountExpired"`
	PrincipalType           string         `json:"principalType"`
	FullName                string         `json:"fullName"`
	Password                string         `json:"password"`
	PreferredDataStreams    []string       `json:"preferredDataStreams"`
	PreferredDataProcessors []string       `json:"preferredDataProcessors"`
	PreferredDataSinks      []string       `json:"preferredDataSinks"`
	UserApiTokens           []UserApiToken `json:"userApiTokens"`
	HideTutorial            bool           `json:"hideTutorial"`
	DarkMode                bool           `json:"darkMode"`
}

type UserApiToken struct {
	TokenID   string `json:"tokenId"`
	TokenName string `json:"tokenName"`
}
