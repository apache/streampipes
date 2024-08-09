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

type ValueSpecification struct {
	ElementID string  `json:"elementId,omitempty"`
	MinValue  int     `json:"minValue,omitempty"`
	MaxValue  int     `json:"maxValue,omitempty"`
	Step      float64 `json:"step,omitempty"`
}

type EventProperty struct {
	ElementID          string             `json:"elementId"`
	Label              string             `json:"label,omitempty"`
	Description        string             `json:"description,omitempty"`
	RuntimeName        string             `json:"runtimeName,omitempty"`
	DomainProperties   []string           `json:"domainProperties,omitempty"`
	PropertyScope      string             `json:"propertyScope,omitempty"`
	RuntimeID          string             `json:"runtimeId,omitempty"`
	RuntimeType        string             `json:"runtimeType"`
	MeasurementUnit    string             `json:"measurementUnit,omitempty"`
	ValueSpecification ValueSpecification `json:"valueSpecification,omitempty"`
}

type EventProperties struct {
	ElementID          string            `json:"elementId"`
	Label              string            `json:"label"`
	Description        string            `json:"description"`
	RuntimeName        string            `json:"runtimeName"`
	DomainProperties   []string          `json:"domainProperties"`
	PropertyScope      string            `json:"propertyScope"`
	RuntimeID          string            `json:"runtimeId"`
	RuntimeType        string            `json:"runtimeType,omitempty"`
	MeasurementUnit    string            `json:"measurementUnit,omitempty"`
	ValueSpecification string            `json:"valueSpecification,omitempty"`
	InEventProperties  []EventProperties `json:"eventProperties,omitempty"`
	InEventProperty    EventProperty     `json:"eventProperty,omitempty"`
}

type EventSchema struct {
	EventProperties []EventProperties `json:"eventProperties"`
}

type DataSeries struct {
	Total   int               `json:"total"`
	Rows    [][]string        `json:"rows"`
	Headers []string          `json:"http_headers"`
	Tags    map[string]string `json:"tags"`
}

type ResponseMessage struct {
	Success       bool           `json:"success"`
	ElementName   interface{}    `json:"elementName"`
	Notifications []Notification `json:"notifications"`
}

type Notification struct {
	Title                 string `json:"title"`
	Description           string `json:"description"`
	AdditionalInformation string `json:"additionalInformation"`
}
