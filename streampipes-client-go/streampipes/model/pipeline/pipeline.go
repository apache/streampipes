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
	//HealthStatus          string                    `json:"healthStatus"`  //枚举类型，OK, REQUIRES_ATTENTION, FAILURE
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
	//ServiceTagPrefix      interface{}               `json:"serviceTagPrefix"`
	OutputStream     SpDataStream     `json:"outputStream"`
	OutputStrategies []OutputStrategy `json:"outputStrategies"`
	PathName         string           `json:"pathName"`
	Category         []string         `json:"category"` // 可能需要更精确的类型
	Rev              string           `json:"_rev"`
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
	InternallyManaged      bool              // 假设它是一个interface{}切片，或者你可以指定为其他类型
	EventGrounding         EventGrounding    `json:"eventGrounding"`
	EventSchema            model.EventSchema `json:"eventSchema"`
	Category               []string          `json:"category"`
	Index                  int32             `json:"index"`
	CorrespondingAdapterId string            `json:"correspondingAdapterId"`
	Rev                    string            `json:"_rev"` // 通常使用驼峰命名法，这里保持原样
}

type EventGrounding struct {
	TransportProtocols []TransportProtocol `json:"transportProtocols"`
	TransportFormats   []TransportFormat   `json:"transportFormats"`
}

type TransportProtocol struct {
	ElementId       string          `json:"elementId"`
	BrokerHostname  string          `json:"brokerHostname"`
	TopicDefinition TopicDefinition `json:"topicDefinition"`
	Class           string          `json:"@class,omitempty"` // 假设@class是JSON中的特殊字段
}

type TopicDefinition struct {
	ActualTopicName string `json:"actualTopicName"`
	Class           string `json:"@class"` // 使用json标签映射到JSON中的@class字段
}

type TransportFormat struct {
	RdfType []string `json:"rdfType"` // 假设rdfType是一个URI的切片
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
	StaticPropertyType StaticPropertyType `json:"staticPropertyType"` //不一定要
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
	// Class 字段用来模拟 @class 注解，但在Go中通常不会这样命名
	// 如果你确实需要存储类信息，可以将其作为一个普通的字符串字段
	Class string `json:"class,omitempty"` // omitempty 表示在序列化时如果该字段为空则忽略
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
	// 去掉了一个枚举类型
	Category []string `json:"category"`
	Rev      string   `json:"_rev"`
}
