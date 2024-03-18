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

import "testing"

func equal(x, y []string) bool {
	if len(x) != len(y) {
		return false
	}
	for i := range x {
		if x[i] != y[i] {
			return false
		}
	}
	return true
}

func TestEqual(t *testing.T) {
	t.Run("equal slices", func(t *testing.T) {
		x := []string{"a", "b", "c"}
		y := []string{"a", "b", "c"}
		if !equal(x, y) {
			t.Errorf("expected equal(%v, %v) to return true, got false", x, y)
		}
	})

	t.Run("different lengths", func(t *testing.T) {
		x := []string{"a", "b", "c"}
		y := []string{"a", "b"}
		if equal(x, y) {
			t.Errorf("expected equal(%v, %v) to return false, got true", x, y)
		}
	})

	t.Run("different elements", func(t *testing.T) {
		x := []string{"a", "b", "c"}
		y := []string{"a", "b", "d"}
		if equal(x, y) {
			t.Errorf("expected equal(%v, %v) to return false, got true", x, y)
		}
	})

	t.Run("empty slices", func(t *testing.T) {
		x := []string{}
		y := []string{}
		if !equal(x, y) {
			t.Errorf("expected equal(%v, %v) to return true, got false", x, y)
		}
	})
}

func TestNewStreamPipesApiPath(t *testing.T) {
	initialItems := []string{"test"}
	path := NewStreamPipesApiPath(initialItems)
	if path == nil {
		t.Error("expected non-nil path")
	}
	if !equal(initialItems, path.pathItems) {
		t.Errorf("expected path items %v, got %v", initialItems, path.pathItems)
	}
}

func TestFromStreamPipesBasePath(t *testing.T) {
	path := NewStreamPipesApiPath([]string{"test"})
	path = path.FromStreamPipesBasePath()
	expected := []string{"test", "streampipes-backend"}
	if !equal(expected, path.pathItems) {
		t.Errorf("expected path items %v, got %v", expected, path.pathItems)
	}
}

func TestAddToPath(t *testing.T) {
	path := NewStreamPipesApiPath([]string{"test"})
	subPaths := []string{"sub1", "sub2"}
	path = path.AddToPath(subPaths)
	expected := []string{"test", "sub1", "sub2"}
	if !equal(expected, path.pathItems) {
		t.Errorf("expected path items %v, got %v", expected, path.pathItems)
	}
}

func TestString(t *testing.T) {
	path := NewStreamPipesApiPath([]string{"test"})
	str := path.String()
	if str != "test" {
		t.Errorf("expected string %s, got %s", "test", str)
	}

	path = path.AddToPath([]string{"sub1", "sub2"})
	str = path.String()
	if str != "test/sub1/sub2" {
		t.Errorf("expected string %s, got %s", "test/sub1/sub2", str)
	}
}
