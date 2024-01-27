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

package resource

import "streampipes-client-go/streampipes/model"

type DataLakeMeasure struct {
	ClassName         string            `alias:"@class" default:"org.apache.streampipes.model.datalake.DataLakeMeasure"`
	MeasureName       string            `json:"measureName"`
	TimestampField    string            `json:"timestampField"`
	EventSchema       model.EventSchema `json:"eventSchema,omitempty"`
	PipelineId        string            `json:"pipelineId,omitempty"`
	PipelineName      string            `json:"pipelineName,omitempty"`
	PipelineIsRunning bool              `json:"pipelineIsRunning"`
	SchemaVersion     string            `json:"schemaVersion,omitempty"`
	//SchemaUpdateStrategy  DataLakeMeasureSchemaUpdateStrategy `json:"schemaUpdateStrategy"`
	ElementId string `json:"elementId"`
	Rev       string `json:"_rev"`
}

func (d *DataLakeMeasure) GetElementId() string {
	return d.ElementId
}

func (d *DataLakeMeasure) SetElementId(elementId string) {
	d.ElementId = elementId
}

func (d *DataLakeMeasure) GetRev() string {
	return d.Rev
}

func (d *DataLakeMeasure) SetRev(rev string) {
	d.Rev = rev
}

func (d *DataLakeMeasure) GetMeasureName() string {
	return d.MeasureName
}

func (d *DataLakeMeasure) SetMeasureName(measureName string) {
	d.MeasureName = measureName
}

func (d *DataLakeMeasure) GetTimestampField() string {
	return d.TimestampField
}

func (d *DataLakeMeasure) SetTimestampField(timestampField string) {
	d.TimestampField = timestampField
}

func (d *DataLakeMeasure) GetEventSchema() model.EventSchema {
	return d.EventSchema
}

func (d *DataLakeMeasure) SetEventSchema(eventSchema model.EventSchema) {
	d.EventSchema = eventSchema
}

func (d *DataLakeMeasure) GetPipelineId() string {
	return d.PipelineId
}

func (d *DataLakeMeasure) SetPipelineId(pipelineId string) {
	d.PipelineId = pipelineId
}

func (d *DataLakeMeasure) GetPipelineName() string {
	return d.PipelineName
}

func (d *DataLakeMeasure) SetPipelineName(pipelineName string) {
	d.PipelineName = pipelineName
}
func (d *DataLakeMeasure) IsPipelineIsRunning() bool {
	return d.PipelineIsRunning
}

func (d *DataLakeMeasure) SetPipelineIsRunning(pipelineIsRunning bool) {
	d.PipelineIsRunning = pipelineIsRunning
}
func (d *DataLakeMeasure) GetSchemaVersion() string {
	return d.SchemaVersion
}

func (d *DataLakeMeasure) SetSchemaVersion(schemaVersion string) {
	d.SchemaVersion = schemaVersion
}
