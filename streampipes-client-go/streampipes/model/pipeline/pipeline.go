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

package pipeline

import (
	"github.com/apache/streampipes/streampipes-client-go/streampipes/model"
)

type PipelineHealthStatus string

const (
	OK                 PipelineHealthStatus = "OK"
	REQUIRES_ATTENTION PipelineHealthStatus = "REQUIRES_ATTENTION"
	FAILURE            PipelineHealthStatus = "FAILURE"
)

type Pipeline struct {
	Sepas                 []DataProcessorInvocation //`json:"sepas"`
	Streams               []model.SpDataStream      //`json:"streams"`
	Name                  string                    //`json:"name"`
	Description           string                    //`json:"description,omitempty"`
	Actions               []DataSinkInvocation      //`json:"actions"`
	Running               bool                      //`json:"running"`
	RestartOnSystemReboot bool                      //`json:"restartOnSystemReboot"`
	Valid                 bool                      //`json:"valid"`
	StartedAt             int64                     //`json:"startedAt,omitempty"`
	CreatedAt             int64                     //`json:"createdAt"`
	PublicElement         bool                      //`json:"publicElement"`
	CreatedByUser         string                    //`json:"createdByUser"`
	PipelineCategories    []string                  //`json:"pipelineCategories"`
	PipelineNotifications []string                  //`json:"pipelineNotifications"`
	HealthStatus          PipelineHealthStatus      //`json:"healthStatus"`
	ID                    string                    //`json:"_id,omitempty"`
	Rev                   string                    //`json:"_rev,omitempty"`
}

type DataProcessorInvocation struct {
	ElementId             string                    `json:"elementId"`
	Dom                   string                    `json:"dom"`
	ConnectedTo           []string                  `json:"connectedTo"`
	Name                  string                    `json:"name"`
	Description           string                    `json:"description"`
	IconUrl               string                    `json:"iconUrl"`
	AppId                 string                    `json:"appId"`
	IncludesAssets        bool                      `json:"includesAssets"`
	IncludesLocales       bool                      `json:"includesLocales"`
	IncludedAssets        []string                  `json:"includedAssets"`
	IncludedLocales       []string                  `json:"includedLocales"`
	InternallyManaged     bool                      `json:"internallyManaged"`
	Version               int32                     `json:"version"`
	InputStreams          []model.SpDataStream      `json:"inputStreams"`
	StaticProperties      []model.StaticProperty    `json:"staticProperties"`
	BelongsTo             string                    `json:"belongsTo"`
	StatusInfoSettings    ElementStatusInfoSettings `json:"statusInfoSettings"`
	SupportedGrounding    model.EventGrounding      `json:"supportedGrounding"`
	CorrespondingPipeline string                    `json:"correspondingPipeline"`
	CorresponddingUser    string                    `json:"correspondingUser"`
	StreamRequirements    []model.SpDataStream      `json:"streamRequirements"`
	Configured            bool                      `json:"configured"`
	Uncompleted           bool                      `json:"uncompleted"`
	SelectedEndpointUrl   string                    `json:"selectedEndpointUrl"`
	OutputStream          model.SpDataStream        `json:"outputStream"`
	OutputStrategies      []OutputStrategy          `json:"outputStrategies"`
	PathName              string                    `json:"pathName"`
	Category              []string                  `json:"category"`
	Rev                   string                    `json:"_rev"`
}

type ElementStatusInfoSettings struct {
	ElementIdentifier string `json:"elementIdentifier"`
	KafkaHost         string `json:"kafkaHost"`
	KafkaPort         int32  `json:"kafkaPort"`
	ErrorTopic        string `json:"errorTopic"`
	StatsTopic        string `json:"statsTopic"`
}

type OutputStrategy struct {
	Name        string               `json:"name"`
	RenameRules []PropertyRenameRule `json:"renameRules"`
	Class       string               `json:"class,omitempty"`
}

type PropertyRenameRule struct {
	RuntimeID      string `json:"runtimeId"`
	NewRuntimeName string `json:"newRuntimeName"`
}

type DataSinkInvocation struct {
	ElementId             string                    `json:"elementId"`
	Dom                   string                    `json:"dom"`
	ConnectedTo           []string                  `json:"connectedTo"`
	Name                  string                    `json:"name"`
	Description           string                    `json:"description"`
	IconUrl               string                    `json:"iconUrl"`
	AppId                 string                    `json:"appId"`
	IncludesAssets        bool                      `json:"includesAssets"`
	IncludesLocales       bool                      `json:"includesLocales"`
	IncludedAssets        []string                  `json:"includedAssets"`
	IncludedLocales       []string                  `json:"includedLocales"`
	InternallyManaged     bool                      `json:"internallyManaged"`
	Version               int32                     `json:"version"`
	InputStreams          []model.SpDataStream      `json:"inputStreams"`
	StaticProperties      []model.StaticProperty    `json:"staticProperties"`
	BelongsTo             string                    `json:"belongsTo"`
	StatusInfoSettings    ElementStatusInfoSettings `json:"statusInfoSettings"`
	SupportedGrounding    model.EventGrounding      `json:"supportedGrounding"`
	CorrespondingPipeline string                    `json:"correspondingPipeline"`
	CorrespondingUser     string                    `json:"correspondingUser"`
	StreamRequirements    []model.SpDataStream      `json:"streamRequirements"`
	Configured            bool                      `json:"configured"`
	Uncompleted           bool                      `json:"uncompleted"`
	SelectedEndpointUrl   string                    `json:"selectedEndpointUrl"`
	Category              []string                  `json:"category"`
	Rev                   string                    `json:"_rev"`
}

type PipelineElementStatus struct {
	ElementID       string `json:"elementId"`
	ElementName     string `json:"elementName"`
	OptionalMessage string `json:"optionalMessage"`
	Success         bool   `json:"success"`
}

type PipelineOperationStatus struct {
	PipelineId    string                  `json:"pipelineId"`
	PipelineName  string                  `json:"pipelineName"`
	Title         string                  `json:"title"`
	Success       bool                    `json:"success"`
	ElementStatus []PipelineElementStatus `json:"elementStatus"`
}

type PipelineStatusMessage struct {
	PipelineId  string `json:"pipelineId"`
	Timestamp   int64  `json:"timestamp"`
	MessageType string `json:"messageType"`
	Message     string `json:"message"`
}
type PipelineElementValidationLevel string

const (
	ValidationInfo  PipelineElementValidationLevel = "INFO"
	ValidationError PipelineElementValidationLevel = "ERROR"
)

type PipelineElementValidationInfo struct {
	Level   PipelineElementValidationLevel `json:"level"`
	Message string                         `json:"message"`
}
