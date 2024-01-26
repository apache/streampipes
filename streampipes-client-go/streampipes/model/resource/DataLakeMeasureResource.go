package resource

import (
	"streampipes-client-go/streampipes/model"
)

type DataLakeMeasure struct {
	ClassName         string            `alias:"@class" default:"org.apache.streampipes.model.datalake.DataLakeMeasure"`
	MeasureName       string            `json:"measureName"`
	TimestampField    string            `json:"timestampField"`
	EventSchema       model.EventSchema `json:"eventSchema,omitempty"`
	PipelineId        string            `json:"pipelineId,omitempty"`
	PipelineName      string            `json:"pipelineName,omitempty"`
	PipelineIsRunning bool              `json:"pipelineIsRunning"`
	SchemaVersion     string            `json:"schemaVersion,omitempty"`
	//SchemaUpdateStrategy string
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
