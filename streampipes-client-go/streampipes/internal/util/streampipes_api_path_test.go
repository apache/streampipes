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
	"github.com/stretchr/testify/assert"
	"testing"
)

func TestNewStreamPipesApiPath(t *testing.T) {
	initialItems := []string{"test"}
	path := NewStreamPipesApiPath(initialItems)
	assert.NotNil(t, path)
	assert.Equal(t, initialItems, path.pathItems)
}

func TestFromStreamPipesBasePath(t *testing.T) {
	path := NewStreamPipesApiPath([]string{"test"})
	path = path.FromStreamPipesBasePath()
	assert.Equal(t, []string{"test", "streampipes-backend"}, path.pathItems)
}

func TestAddToPath(t *testing.T) {
	path := NewStreamPipesApiPath([]string{"test"})
	subPaths := []string{"sub1", "sub2"}
	path = path.AddToPath(subPaths)
	assert.Equal(t, []string{"test", "sub1", "sub2"}, path.pathItems)
}

func TestString(t *testing.T) {
	path := NewStreamPipesApiPath([]string{"test"})
	str := path.String()
	assert.Equal(t, "test", str)

	path = path.AddToPath([]string{"sub1", "sub2"})
	str = path.String()
	assert.Equal(t, "test/sub1/sub2", str)
}
