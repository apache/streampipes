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

package adapter

import "github.com/apache/streampipes/streampipes-client-go/streampipes/model"

type AdapterDescription struct {
	ElementID                        string                                 `json:"elementId"`
	Rev                              string                                 `json:"rev"`
	DOM                              string                                 `json:"dom"`
	ConnectedTo                      []string                               `json:"connectedTo"`
	Name                             string                                 `json:"name"`
	Description                      string                                 `json:"description"`
	AppID                            string                                 `json:"appId"`
	IncludesAssets                   bool                                   `json:"includesAssets"`
	IncludesLocales                  bool                                   `json:"includesLocales"`
	IncludedAssets                   []string                               `json:"includedAssets"`
	IncludedLocales                  []string                               `json:"includedLocales"`
	InternallyManaged                bool                                   `json:"internallyManaged"`
	Version                          int32                                  `json:"version"`
	DataStream                       model.SpDataStream                     `json:"dataStream"`
	Running                          bool                                   `json:"running"`
	EventGrounding                   model.EventGrounding                   `json:"eventGrounding"`
	Icon                             string                                 `json:"icon"`
	Config                           []model.StaticProperty                 `json:"config"`
	Rules                            []model.TransformationRuleDescription  `json:"rules"`
	Category                         []string                               `json:"category"`
	CreatedAt                        int64                                  `json:"createdAt"`
	SelectedEndpointURL              string                                 `json:"selectedEndpointUrl"`
	DeploymentConfiguration          model.ExtensionDeploymentConfiguration `json:"deploymentConfiguration"`
	CorrespondingDataStreamElementID string                                 `json:"correspondingDataStreamElementId"`
	EventSchema                      model.EventSchema                      `json:"eventSchema"`
	ValueRules                       []model.TransformationRuleDescription  `json:"valueRules"`
	StreamRules                      []model.TransformationRuleDescription  `json:"streamRules"`
	SchemaRules                      []model.TransformationRuleDescription  `json:"schemaRules"`
}
