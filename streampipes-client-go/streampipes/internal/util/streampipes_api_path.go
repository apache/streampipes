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
	pathItems []string // PathItems stores URL fragments
}

func NewStreamPipesApiPath(initialPathItems []string) *StreamPipesApiPath {

	return &StreamPipesApiPath{
		pathItems: initialPathItems,
	}
}

func (s *StreamPipesApiPath) FromStreamPipesBasePath() *StreamPipesApiPath {
	path := "streampipes-backend"
	s.pathItems = append(s.pathItems, path)
	return s
}

func (s *StreamPipesApiPath) AddToPath(pathItem []string) *StreamPipesApiPath {
	s.pathItems = append(s.pathItems, pathItem...)
	return s
}

func (s *StreamPipesApiPath) String() string {

	if len(s.pathItems) == 1 {
		return s.pathItems[0]
	}
	path := strings.Join(s.pathItems, "/")
	s.pathItems = []string{path}
	return path
}
