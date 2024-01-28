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

// PathItems stores URL fragments
// QueryParameters stores query parameters
type StreamPipesApiPath struct {
	PathItems       []string
	QueryParameters map[string]string
}

func NewStreamPipesApiPath(initialPathItems []string) *StreamPipesApiPath {

	return &StreamPipesApiPath{
		PathItems:       initialPathItems,
		QueryParameters: make(map[string]string),
	}
}

func (s *StreamPipesApiPath) FromStreamPipesBasePath() *StreamPipesApiPath {
	path := "streampipes-backend"
	s.PathItems = append(s.PathItems, path)
	return s
}

func (s *StreamPipesApiPath) FromBaseApiPath() *StreamPipesApiPath {
	initialPaths := []string{"streampipes-backend", "api", "v2"}
	s.PathItems = append(s.PathItems, initialPaths...)
	return s
}

func (s *StreamPipesApiPath) FromStreamPipesBasePathWithSubPaths(allSubPaths []string) *StreamPipesApiPath {
	return s.FromStreamPipesBasePath().AddToPath(allSubPaths)
}

func (s *StreamPipesApiPath) AddToPath(pathItem []string) *StreamPipesApiPath {
	s.PathItems = append(s.PathItems, pathItem...)
	return s
}

func (s *StreamPipesApiPath) WithQueryParameters(queryParameters map[string]string) *StreamPipesApiPath {
	for key, value := range queryParameters {
		s.QueryParameters[key] = value
	}
	return s
}

func (s *StreamPipesApiPath) ToString() string {
	//Splicing URLs
	//Query parameter concatenation is still being implemented
	if len(s.PathItems) == 1 {
		return s.PathItems[0]
	}
	path := strings.Join(s.PathItems, "/")
	//todo
	s.PathItems = []string{path}
	return path
}

// Splicing query parameters into a URL : ? or &
//func (s *StreamPipesApiPath) AppendQueryParameters() string {
//	if len(s.QueryParameters) == 0 {
//		return s.PathItems[0]
//	}
//	var queryParams []string
//	for key, value := range s.QueryParameters {
//		queryParams = append(queryParams, fmt.Sprintf("%s=%s", applyEncoding(key), applyEncoding(value)))
//	}
//
//	queryString := strings.Join(queryParams, "&")
//	return fmt.Sprintf("%s?%s", s.PathItems[0], queryString)
//}

// Escaping query parameters, which can be safely used in URL query parameters.
//func applyEncoding(value string) string {
//	return url.QueryEscape(value)
//}
