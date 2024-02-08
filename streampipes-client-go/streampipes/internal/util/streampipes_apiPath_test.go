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
	"testing"
)

func TestNewStreamPipesApiPath(t *testing.T) {
	pathItems := []string{"part1", "part2"}
	path := NewStreamPipesApiPath(pathItems)
	if len(path.PathItems) != len(pathItems) {
		t.Errorf("Expected path items length to be %d, got %d", len(pathItems), len(path.PathItems))
	}
}

func TestFromStreamPipesBasePath(t *testing.T) {
	path := NewStreamPipesApiPath([]string{"part1", "part2"}).FromStreamPipesBasePath()
	expectedPathItems := []string{"part1", "part2", "streampipes-backend"}
	if len(path.PathItems) != len(expectedPathItems) {
		t.Errorf("Expected path items length to be %d, got %d", len(expectedPathItems), len(path.PathItems))
	}
}

func TestFromStreamPipesBasePathWithSubPaths(t *testing.T) {
	path := NewStreamPipesApiPath([]string{"part1", "part2"}).FromStreamPipesBasePathWithSubPaths([]string{"sub1", "sub2"})
	expectedPathItems := []string{"part1", "part2", "streampipes-backend", "sub1", "sub2"}
	if len(path.PathItems) != len(expectedPathItems) {
		t.Errorf("Expected path items length to be %d, got %d", len(expectedPathItems), len(path.PathItems))
	}
}

func TestAddToPath(t *testing.T) {
	path := NewStreamPipesApiPath([]string{"part1", "part2"}).AddToPath([]string{"sub1", "sub2"})
	expectedPathItems := []string{"part1", "part2", "sub1", "sub2"}
	if len(path.PathItems) != len(expectedPathItems) {
		t.Errorf("Expected path items length to be %d, got %d", len(expectedPathItems), len(path.PathItems))
	}
}

func TestToString(t *testing.T) {
	path := StreamPipesApiPath{
		PathItems: []string{"part1", "part2"},
	}
	expected := "part1/part2"
	actual := path.ToString()
	if actual != expected {
		t.Errorf("Expected %s, got %s", expected, actual)
	}
}
