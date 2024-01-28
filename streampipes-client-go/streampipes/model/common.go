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
	"math/rand"
	"time"
)

// Generate random letters
func RandomLetters(length int) string {
	rand.Seed(time.Now().UnixNano())
	letterRunes := []rune("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ") //Define the alphabet
	result := make([]rune, length)
	for i := range result {
		result[i] = letterRunes[rand.Intn(len(letterRunes))]
	}
	return string(result)
}

type ValueSpecification struct {
	ClassName string  `json:"@class,omitempty"`
	ElementID string  `json:"elementId,omitempty"`
	MinValue  int     `json:"minValue,omitempty"`
	MaxValue  int     `json:"maxValue,omitempty"`
	Step      float64 `json:"step,omitempty"`
}

type EventProperty struct {
	ClassName          string             `alias:"@class" default:"org.apache.streampipes.model.schema.EventPropertyPrimitive"`
	ElementID          string             `json:"elementId"`
	Label              string             `json:"label,omitempty"`
	Description        string             `json:"description,omitempty"`
	RuntimeName        string             `json:"runtimeName,omitempty"`
	Required           bool               `json:"required,omitempty"`
	DomainProperties   []string           `json:"domainProperties,omitempty"`
	PropertyScope      string             `json:"propertyScope,omitempty"`
	Index              int                `json:"index"`
	RuntimeID          string             `json:"runtimeId,omitempty"`
	RuntimeType        string             `json:"runtimeType"`
	MeasurementUnit    string             `json:"measurementUnit,omitempty"`
	ValueSpecification ValueSpecification `json:"valueSpecification,omitempty"`
}

type EventProperties struct {
	Class              string            `json:"@class"`
	ElementID          string            `json:"elementId"`
	Label              string            `json:"label"`
	Description        string            `json:"description"`
	RuntimeName        string            `json:"runtimeName"`
	Required           bool              `json:"required"`
	DomainProperties   []string          `json:"domainProperties"`
	PropertyScope      string            `json:"propertyScope"`
	Index              int               `json:"index"`
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

type ApplicationLink struct {
	ClassName              string `json:"@class,omitempty"`
	ElementID              string `json:"elementId,omitempty"`
	ApplicationName        string `json:"applicationName,omitempty"`
	ApplicationDescription string `json:"applicationDescription,omitempty"`
	ApplicationURL         string `json:"applicationUrl,omitempty"`
	ApplicationIconUrl     string `json:"applicationIcon_url,omitempty"`
	ApplicationLinkType    string `json:"applicationLinkType,omitempty"`
}

type TopicDefinition struct {
	ClassName       string `json:"@class,omitempty"`
	ActualTopicName string `json:"actualTopicName"`
}

type TransportProtocol struct {
	ClassName       string          `json:"@class"`
	ElementId       string          `json:"elementId"`
	BrokerHostname  string          `json:"brokerHostname"`
	TopicDefinition TopicDefinition `json:"topicDefinition"`
	Port            int             `json:"kafkaPort"`
}

type TransportFormat struct {
	RdfType []string `json:"rdfType"`
}

type EventGrounding struct {
	TransportProtocols []TransportProtocol `json:"transportProtocols"`
	TransportFormats   []TransportFormat   `json:"transportFormats"`
}

type MeasurementCapability struct {
	Capability string `json:"capability,omitempty"`
	ElementId  string `json:"elementId,omitempty"`
}

type MeasurementObject struct {
	ElementId      string `json:"elementId,omitempty"`
	MeasuresObject string `json:"measuresObject,omitempty"`
}

type DataSerie struct {
	Total   int             `json:"total"`
	Rows    [][]interface{} `json:"rows"`
	Headers []string        `json:"headers"`
	Tags    string          `json:"tags"`
}

func (e *EventSchema) GetEventProperties() []EventProperties {
	return e.EventProperties
}

func (e *EventSchema) SetEventProperties(eventProperties []EventProperties) {
	e.EventProperties = eventProperties
}

func (e *EventSchema) AddEventProperty(eventProperty []EventProperties) {
	e.EventProperties = append(e.EventProperties, eventProperty...)
}
