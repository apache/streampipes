package pipeline

import (
	"github.com/apache/streampipes/streampipes-client-go/streampipes/model"
)

type Pipeline struct {
	Sepas                 []DataProcessorInvocation `json:"sepas"`
	Streams               []SpDataStream            `json:"streams"`
	Name                  string                    `json:"name"`
	Description           string                    `json:"description,omitempty"`
	Actions               []DataSinkInvocation      `json:"actions"`
	Running               bool                      `json:"running"`
	RestartOnSystemReboot bool                      `json:"restartOnSystemReboot"`
	Valid                 bool                      `json:"valid"`
	StartedAt             int64                     `json:"startedAt,omitempty"`
	CreatedAt             int64                     `json:"createdAt"`
	PublicElement         bool                      `json:"publicElement"`
	CreatedByUser         string                    `json:"createdByUser"`
	PipelineCategories    []string                  `json:"pipelineCategories"`
	PipelineNotifications []string                  `json:"pipelineNotifications"`
	//HealthStatus          string                    `json:"healthStatus"`  //OK, REQUIRES_ATTENTION, FAILURE
	ID  string `json:"_id,omitempty"`
	Rev string `json:"_rev,omitempty"`
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
	InputStreams          []SpDataStream            `json:"inputStreams"`
	StaticProperties      []StaticProperty          `json:"staticProperties"`
	BelongsTo             string                    `json:"belongsTo"`
	StatusInfoSettings    ElementStatusInfoSettings `json:"statusInfoSettings"`
	SupportedGrounding    EventGrounding            `json:"supportedGrounding"`
	CorrespondingPipeline string                    `json:"correspondingPipeline"`
	CorresponddingUser    string                    `json:"correspondingUser"`
	StreamRequirements    []SpDataStream            `json:"streamRequirements"`
	Configured            bool                      `json:"configured"`
	Uncompleted           bool                      `json:"uncompleted"`
	SelectedEndpointUrl   string                    `json:"selectedEndpointUrl"`
	OutputStream          SpDataStream              `json:"outputStream"`
	OutputStrategies      []OutputStrategy          `json:"outputStrategies"`
	PathName              string                    `json:"pathName"`
	Category              []string                  `json:"category"`
	Rev                   string                    `json:"_rev"`
}

type SpDataStream struct {
	ElementId              string            `json:"elementId"`
	Dom                    string            `json:"dom"`
	ConnectedTo            []string          `json:"connectedTo"`
	Name                   string            `json:"name"`
	Description            string            `json:"description"`
	IconUrl                string            `json:"iconUrl"`
	AppId                  string            `json:"appId"`
	IncludesAssets         bool              `json:"includesAssets"`
	IncludesLocales        bool              `json:"includesLocales"`
	IncludedAssets         []string          `json:"includedAssets"`
	IncludedLocales        []string          `json:"includedLocales"`
	InternallyManaged      bool              `json:"internallyManaged"`
	EventGrounding         EventGrounding    `json:"eventGrounding"`
	EventSchema            model.EventSchema `json:"eventSchema"`
	Category               []string          `json:"category"`
	Index                  int32             `json:"index"`
	CorrespondingAdapterId string            `json:"correspondingAdapterId"`
	Rev                    string            `json:"_rev"`
}

type EventGrounding struct {
	TransportProtocols []TransportProtocol `json:"transportProtocols"`
	TransportFormats   []TransportFormat   `json:"transportFormats"`
}

type TransportProtocol struct {
	ElementId       string          `json:"elementId"`
	BrokerHostname  string          `json:"brokerHostname"`
	TopicDefinition TopicDefinition `json:"topicDefinition"`
	Class           string          `json:"@class,omitempty"`
}

type TopicDefinition struct {
	ActualTopicName string `json:"actualTopicName"`
	Class           string `json:"@class"`
}

type TransportFormat struct {
	RdfType []string `json:"rdfType"`
}

type StaticPropertyType string

const (
	AnyStaticProperty                        StaticPropertyType = "AnyStaticProperty"
	CodeInputStaticProperty                  StaticPropertyType = "CodeInputStaticProperty"
	CollectionStaticProperty                 StaticPropertyType = "CollectionStaticProperty"
	ColorPickerStaticProperty                StaticPropertyType = "ColorPickerStaticProperty"
	DomainStaticProperty                     StaticPropertyType = "DomainStaticProperty"
	FreeTextStaticProperty                   StaticPropertyType = "FreeTextStaticProperty"
	FileStaticProperty                       StaticPropertyType = "FileStaticProperty"
	MappingPropertyUnary                     StaticPropertyType = "MappingPropertyUnary"
	MappingPropertyNary                      StaticPropertyType = "MappingPropertyNary"
	MatchingStaticProperty                   StaticPropertyType = "MatchingStaticProperty"
	OneOfStaticProperty                      StaticPropertyType = "OneOfStaticProperty"
	RuntimeResolvableAnyStaticProperty       StaticPropertyType = "RuntimeResolvableAnyStaticProperty"
	RuntimeResolvableGroupStaticProperty     StaticPropertyType = "RuntimeResolvableGroupStaticProperty"
	RuntimeResolvableOneOfStaticProperty     StaticPropertyType = "RuntimeResolvableOneOfStaticProperty"
	RuntimeResolvableTreeInputStaticProperty StaticPropertyType = "RuntimeResolvableTreeInputStaticProperty"
	StaticPropertyGroup                      StaticPropertyType = "StaticPropertyGroup"
	StaticPropertyAlternatives               StaticPropertyType = "StaticPropertyAlternatives"
	StaticPropertyAlternative                StaticPropertyType = "StaticPropertyAlternative"
	SecretStaticProperty                     StaticPropertyType = "SecretStaticProperty"
	SlideToggleStaticProperty                StaticPropertyType = "SlideToggleStaticProperty"
)

type StaticProperty struct {
	Optional           bool               `json:"optional,omitempty"`
	StaticPropertyType StaticPropertyType `json:"staticPropertyType"`
	Index              int32              `json:"index"`
	Label              string             `json:"label"`
	Description        string             `json:"description"`
	InternalName       string             `json:"internalName"`
	Predefined         bool               `json:"predefined"`
	Class              string             `json:"@class"`
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
	InputStreams          []SpDataStream            `json:"inputStreams"`
	StaticProperties      []StaticProperty          `json:"staticProperties"`
	BelongsTo             string                    `json:"belongsTo"`
	StatusInfoSettings    ElementStatusInfoSettings `json:"statusInfoSettings"`
	SupportedGrounding    EventGrounding            `json:"supportedGrounding"`
	CorrespondingPipeline string                    `json:"correspondingPipeline"`
	CorrespondingUser     string                    `json:"correspondingUser"`
	StreamRequirements    []SpDataStream            `json:"streamRequirements"`
	Configured            bool                      `json:"configured"`
	Uncompleted           bool                      `json:"uncompleted"`
	SelectedEndpointUrl   string                    `json:"selectedEndpointUrl"`
	Category              []string                  `json:"category"`
	Rev                   string                    `json:"_rev"`
}
