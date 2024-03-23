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

package utils

import (
	"testing"
)

func TestCheckUrl(t *testing.T) {
	tests := []struct {
		name     string
		url      string
		expected bool
	}{
		{name: "Valid HTTP URL", url: "http://streampipes.com:80", expected: true},
		{name: "Valid HTTP URL", url: "http://localhost:80", expected: true},
		{name: "Valid HTTP URL", url: "http://127.0.0.1:80", expected: true},
		{name: "Valid HTTPS URL", url: "https://streampipes.com:443", expected: true},
		{name: "Valid URL with port", url: "http://streampipes.com:8080", expected: true},
		{name: "Valid URL with IPv6", url: "http://[2001:db8:85a3:8d3:1319:8a2e:370:7348]:80", expected: false},
		{name: "Invalid URL scheme", url: "ftp://streampipes.com:80", expected: false},
		{name: "Invalid URL with no port", url: "http://streampipes.com", expected: false},
		{name: "Invalid URL with space", url: "http://streampipes.com: 80", expected: false},
		{name: "Invalid URL with special character", url: "http://streampipes.com:$80", expected: false},
		{name: "Empty URL", url: "", expected: false},
		{name: "Nil URL", url: "", expected: false},
	}

	for _, test := range tests {
		t.Run(test.name, func(t *testing.T) {
			actual := CheckUrl(test.url)
			if actual != test.expected {
				t.Errorf("CheckUrl(%s) = %v; want %v", test.url, actual, test.expected)
			}
		})
	}
}
