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

package data_stream

import "streampipes-client-go/streampipes/model"

type DataStream struct {
	ClassName              string                        `json:"@class"`
	ElementId              string                        `json:"elementId"`
	Name                   string                        `json:"name"`
	Description            string                        `json:"description,omitempty"`
	IconUrl                string                        `json:"iconUrl,omitempty"`
	AppId                  string                        `json:"appId,omitempty"`
	IncludesAssets         bool                          `json:"includesAssets"`
	IncludesLocales        bool                          `json:"includesLocales"`
	IncludedAssets         []string                      `json:"includedAssets,omitempty"`
	IncludedLocales        []string                      `json:"includedLocales,omitempty"`
	ApplicationLinks       []model.ApplicationLink       `json:"applicationLinks,omitempty"`
	InternallyManaged      bool                          `json:"internallyManaged"`
	ConnectedTo            []string                      `json:"connectedTo,omitempty"`
	EventGrounding         model.EventGrounding          `json:"eventGrounding"`
	EventSchema            model.EventSchema             `json:"eventSchema,omitempty"`
	MeasurementCapability  []model.MeasurementCapability `json:"measurementCapability,omitempty"`
	MeasurementObject      []model.MeasurementObject     `json:"measurementObject,omitempty"`
	Index                  int                           `json:"index"`
	CorrespondingAdapterId string                        `json:"correspondingAdapterId,omitempty"`
	Category               []string                      `json:"category,omitempty"`
	URI                    string                        `json:"uri,omitempty"`
	Dom                    string                        `json:"dom,omitempty"`
	Rev                    string                        `json:"_rev,omitempty"`
}
