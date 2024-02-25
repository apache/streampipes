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

package util

import (
	"strings"
)

type StreamPipesApiPath struct {
	PathItems []string // PathItems stores URL fragments
}

func NewStreamPipesApiPath(initialPathItems []string) *StreamPipesApiPath {

	return &StreamPipesApiPath{
		PathItems: initialPathItems,
	}
}

func (s *StreamPipesApiPath) FromStreamPipesBasePath() *StreamPipesApiPath {
	path := "streampipes-backend"
	s.PathItems = append(s.PathItems, path)
	return s
}

func (s *StreamPipesApiPath) FromStreamPipesBasePathWithSubPaths(allSubPaths []string) *StreamPipesApiPath {
	return s.FromStreamPipesBasePath().AddToPath(allSubPaths)
}

func (s *StreamPipesApiPath) AddToPath(pathItem []string) *StreamPipesApiPath {
	s.PathItems = append(s.PathItems, pathItem...)
	return s
}

func (s *StreamPipesApiPath) ToString() string {
	//Splicing URLs
	if len(s.PathItems) == 1 {
		return s.PathItems[0]
	}
	path := strings.Join(s.PathItems, "/")
	s.PathItems = []string{path}
	return path
}

func (s *StreamPipesApiPath) GetBaseUrl(Url string) *StreamPipesApiPath {

	ApiPath := NewStreamPipesApiPath([]string{Url}).FromStreamPipesBasePath()
	ApiPath.ToString()

	return ApiPath
}
