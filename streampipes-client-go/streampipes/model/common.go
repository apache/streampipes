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

type BaseElement struct {
	ElementID string `json:"element_id,omitempty"` //When containing the omitempty attribute, converting JSON substrings through encoding/JSON will ignore null values
}

type ValueSpecification struct {
	BaseElement
	ClassName string  `json:"@class,omitempty"`
	ElementID string  `json:"elementId,omitempty"`
	MinValue  int     `json:"minValue,omitempty"`
	MaxValue  int     `json:"maxValue,omitempty"`
	Step      float64 `json:"step,omitempty"`
}

type EventProperty struct {
	//ClassName          string              `alias:"@class" default:"org.apache.streampipes.model.schema.EventPropertyPrimitive"` //ClassName          string             `json:"@class,omitempty"`
	ElementID          string              `json:"elementId"`
	Label              string              `json:"label,omitempty"`
	Description        string              `json:"description,omitempty"`
	RuntimeName        string              `json:"runtimeName,omitempty"`
	Required           bool                `json:"required,omitempty"`
	DomainProperties   []string            `json:"domainProperties,omitempty"`
	PropertyScope      string              `json:"propertyScope,omitempty"`
	Index              int                 `json:"index"`
	RuntimeID          string              `json:"runtimeId,omitempty"`
	RuntimeType        string              `json:"runtimeType"`
	MeasurementUnit    string              `json:"measurementUnit,omitempty"`
	ValueSpecification *ValueSpecification `json:"valueSpecification,omitempty"`
}

type EventSchema struct {
	EventProperties []EventProperty `json:"eventProperties"`
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
	ActualTopicName string `json:"actual_topic_name"`
}

type TransportProtocol struct {
	ClassName       string          `json:"@class"`
	ElementId       string          `json:"element_id"`
	BrokerHostname  string          `json:"broker_hostname"`
	TopicDefinition TopicDefinition `json:"topic_definition"`
	Port            int             `json:"kafkaPort"`
}

type TransportFormat struct {
	RdfType []string `json:"rdf_type"`
}

type EventGrounding struct {
	TransportProtocols []TransportProtocol `json:"transport_protocols"`
	TransportFormats   []TransportFormat   `json:"transport_formats"`
}

type MeasurementCapability struct {
	Capability string `json:"capability,omitempty"`
	ElementId  string `json:"element_id,omitempty"`
}

type MeasurementObject struct {
	ElementId      string `json:"element_id,omitempty"`
	MeasuresObject string `json:"measures_object,omitempty"`
}

func (e *EventSchema) GetEventProperties() []EventProperty {
	return e.EventProperties
}

func (e *EventSchema) SetEventProperties(eventProperties []EventProperty) {
	e.EventProperties = eventProperties
}

func (e *EventSchema) AddEventProperty(eventProperty []EventProperty) {
	e.EventProperties = append(e.EventProperties, eventProperty...)
}
