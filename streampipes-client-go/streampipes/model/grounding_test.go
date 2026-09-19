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

package model

import (
	"encoding/json"
	"strings"
	"testing"
)

func TestChannelGroundingRoundTrip(t *testing.T) {
	input := `{"topicDefinition":{"@class":"org.apache.streampipes.model.grounding.SimpleTopicDefinition","actualTopicName":"original.topic"},"options":{"groupId":"original-group"}}`
	var grounding EventGrounding
	if err := json.Unmarshal([]byte(input), &grounding); err != nil {
		t.Fatal(err)
	}
	if grounding.TopicDefinition.ActualTopicName != "original.topic" || grounding.Options["groupId"] != "original-group" {
		t.Fatal("channel information was changed")
	}
	encoded, err := json.Marshal(grounding)
	if err != nil {
		t.Fatal(err)
	}
	if strings.Contains(string(encoded), "transportProtocols") {
		t.Fatal("new groundings must not invent legacy brokers")
	}
}
