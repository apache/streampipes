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

package functions

type FunctionId struct {
	Id      string `json:"id"`
	Version int    `json:"version"`
}

type FunctionDefinition struct {
	FunctionIds     FunctionId `json:"functionId"`
	ConsumedStreams []string   `json:"consumedStreams"`
}

type SpLogEntry struct {
	Timestamp    int64        `json:"timestamp"`
	ErrorMessage SpLogMessage `json:"errorMessage"`
}

type SpLogMessage struct {
	Level          string `json:"level"`
	Title          string `json:"title"`
	Detail         string `json:"detail"`
	Cause          string `json:"cause"`
	FullStackTrace string `json:"fullStackTrace"`
}

type SpMetricsEntry struct {
	LastTimestamp int64                     `json:"lastTimestamp"`
	MessagesIn    map[string]MessageCounter `json:"messagesIn"`
	MessagesOut   MessageCounter            `json:"messagesOut"`
}

type MessageCounter struct {
	LastTimestamp int64 `json:"lastTimestamp"`
	Counter       int64 `json:"counter"`
}
