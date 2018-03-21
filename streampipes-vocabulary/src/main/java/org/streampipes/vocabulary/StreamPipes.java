package org.streampipes.vocabulary;

public class StreamPipes {

  public static final String NS = "https://streampipes.org/vocabulary/v1/";
  public static final String NS_PREFIX = "sp";

  // Concepts

  public static final String ANYTHING = NS + "Anything";

  public static final String DATA_PROCESSOR_DESCRIPTION = NS + "DataProcessorDescription";
  public static final String DATA_PROCESSOR_INVOCATION = NS + "DataProcessorInvocation";
  public static final String DATA_SINK_DESCRIPTION = NS + "DataSinkDescription";
  public static final String DATA_SINK_INVOCATION = NS + "DataSinkInvocation";
  public static final String DATA_SOURCE_DESCRIPTION = NS + "DataSourceDescription";
  public static final String ADAPTER_DESCRIPTION = NS + "AdapterDescription";
  public static final String FORMAT_DESCRIPTION_LIST = NS + "FromatDescriptionList";
  public static final String PROTOCOL_DESCRIPTION_LIST = NS + "ProtocolDescriptionList";

  public static final String VALUE_SPECIFICATION = NS + "ValueSpecification";

  public static final String EVENT_PROPERTY = NS + "EventProperty";
  public static final String EVENT_PROPERTY_LIST = NS + "EventPropertyList";
  public static final String EVENT_PROPERTY_NESTED = NS + "EventPropertyNested";
  public static final String EVENT_PROPERTY_PRIMITIVE = NS + "EventPropertyPrimitive";

  public static final String APPEND_OUTPUT_STRATEGY = NS + "AppendOutputStrategy";
  public static final String CUSTOM_OUTPUT_STRATEGY = NS + "CustomOutputStrategy";
  public static final String FIXED_OUTPUT_STRATEGY = NS + "FixedOutputStrategy";
  public static final String LIST_OUTPUT_STRATEGY = NS + "ListOutputStrategy";
  public static final String OUTPUT_STRATEGY = NS + "OutputStrategy";
  public static final String KEEP_OUTPUT_STRATEGY = NS + "KeepOutputStrategy";
  public static final String REPLACE_OUTPUT_STRATEGY = NS + "ReplaceOutputStrategy";
  public static final String TRANSFORM_OUTPUT_STRATEGY = NS + "TransformOutputStrategy";
  public static final String CUSTOM_TRANSFORM_OUTPUT_STRATEGY = NS + "CustomTransformOutputStrategy";
  public static final String RUNTIME_RESOLVABLE_TRANSFORM_OUTPUT_STRATEGY = NS +
          "RuntimeResolvableTransformOutputStrategy";
  public static final String TRANSFORM_OPERATION = NS + "TransformOperation";

  public static final String URI_PROPERTY_MAPPING = NS + "UriPropertyMapping";

  public static final String EVENT_PROPERTY_QUALITY_DEFINITION = NS + "EventPropertyQualityDefinition";
  public static final String EVENT_PROPERTY_QUALITY_REQUIREMENT = NS + "EventPropertyQualityRequirement";
  public static final String EVENT_STREAM_QUALITY_DEFINITION = NS + "EventStreamQualityDefinition";
  public static final String Event_STREAM_QUALITY_REQUIREMENT = NS + "EventStreamQualityRequirement";

  public static final String MEASUREMENT_CAPABILITY = NS + "MeasurementCapability";
  public static final String MEASUREMENT_OBJECT = NS + "MeasurementObject";

  public static final String SELECTION_STATIC_PROPERTY = NS + "SelectionStaticProperty";
  public static final String RUNTIME_RESOLVABLE_SELECTION_STATIC_PROPERTY = NS +
          "RuntimeResolvableSelectionStaticProperty";
  public static final String RUNTIME_RESOLVABLE_ANY_STATIC_PROPERTY = NS + "RuntimeResolvableAnyStaticProperty";
  public static final String RUNTIME_RESOLVABLE_ONE_OF_STATIC_PROPERTY = NS + "RuntimeResolvableOneOfStaticProperty";
  public static final String ANY_STATIC_PROPERTY = NS + "AnyStaticProperty";
  public static final String COLLECTION_STATIC_PROPERTY = NS + "CollectionStaticProperty";
  public static final String DOMAIN_STATIC_PROPERTY = NS + "DomainStaticProperty";
  public static final String FREE_TEXT_STATIC_PROPERTY = NS + "FreeTextStaticProperty";
  public static final String MAPPING_PROPERTY = NS + "MappingProperty";
  public static final String MAPPING_PROPERTY_UNARY = NS + "MappingPropertyUnary";
  public static final String MAPPING_PROPERTY_NARY = NS + "MappingPropertyNary";
  public static final String MATCHING_STATIC_PROPERTY = NS + "MatchingStaticProperty";
  public static final String ONE_OF_STATIC_PROPERTY = NS + "OneOfStaticProperty";
  public static final String OPTION = NS + "Option";
  public static final String REMOTE_ONE_OF_STATIC_PROPERTY = NS + "RemoteOneOfStaticProperty";
  public static final String STATIC_PROPERTY = NS + "StaticProperty";
  public static final String SUPPORTED_PROPERTY = NS + "SupportedProperty";

  public static final String APPLICATION_LINK = NS + "ApplicationLink";
  public static final String ELEMENT_STATUS_INFO_SETTINGS = NS + "ElementStatusInfoSettings";

  public static final String EVENT_GROUNDING = NS + "DataStreamGrounding";
  public static final String EVENT_SCHEMA = NS + "EventSchema";
  public static final String DATA_SOURCE = NS + "DataSource";
  public static final String DATA_STREAM = NS + "DataStream";

  public static final String JMS_TRANSPORT_PROTOCOL = NS + "JmsTransportProtocol";
  public static final String KAFKA_TRANSPORT_PROTOCOL = NS + "KafkaTransportProtocol";
  public static final String TRANSPORT_FORMAT = NS + "TransportFormat";
  public static final String TRANSPORT_PROTOCOL = NS + "TransportProtocol";

  public static final String TOPIC_DEFINITION = NS + "TopicDefinition";
  public static final String SIMPLE_TOPIC_DEFINITION = NS + "SimpleTopicDefinition";
  public static final String WILDCARD_TOPIC_DEFINITION = NS + "WildcardTopicDefinition";
  public static final String WILDCARD_TOPIC_MAPPING = NS + "WildcardTopicMapping";

  // Properties

  public static final String REQUIRES_STREAM = NS + "requiresStream";
  public static final String RECEIVES_STREAM = NS + "receivesStream";
  public static final String HAS_STATIC_PROPERTY = NS + "hasStaticProperty";
  public static final String SUPPORTED_GROUNDING = NS + "supportedGrounding";
  public static final String BELONGS_TO = NS + "belongsTo";
  public static final String CORRESPONDING_PIPELINE = NS + "correspondingPipeline";
  public static final String STATUS_INFO_SETTINGS = NS + "statusInfoSettings";

  public static final String ICON_URL = NS + "iconUrl";
  public static final String HAS_URI = NS + "hasUri";
  public static final String HAS_APPLICATION_LINK = NS + "hasApplicationLink";
  public static final String HAS_ELEMENT_NAME = NS + "hasElementName";

  public static final String HAS_OUTPUT_STRATEGY = NS + "hasOutputStrategy";
  public static final String HAS_EPA_TYPE = NS + "hasEpaType";
  public static final String HAS_EC_TYPE = NS + "hasEcType";
  public static final String PRODUCES = NS + "produces";

  public static final String HAS_TRANSPORT_PROTOCOL = NS + "hasTransportProtocol";
  public static final String HAS_TRANSPORT_FORMAT = NS + "hasTransportFormat";
  public static final String JMS_PORT = NS + "jmsPort";

  public static final String ZOOKEEPER_HOST = NS + "zookeeperHost";
  public static final String ZOOKEEPER_PORT = NS + "zookeeperPort";
  public static final String KAFKA_PORT = NS + "kafkaPort";

  public static final String BROKER_HOSTNAME = NS + "brokerHostname";
  public static final String TOPIC = NS + "topic";

  public static final String ELEMENT_IDENTIFIER = NS + "elementIdentifier";
  public static final String KAFKA_HOST = NS + "kafkaHost";
  public static final String ERROR_TOPIC = NS + "errorTopic";
  public static final String STATS_TOPIC = NS + "statsTopic";

  public static final String APPENDS_PROPERTY = NS + "appendsProperty";
  public static final String PRODUCES_PROPERTY = NS + "producesProperty";
  public static final String OUTPUT_RIGHT = NS + "outputRight";
  public static final String HAS_TRANSFORM_OPERATION = NS + "hasTransformOperation";
  public static final String HAS_SOURCE_PROPERTY_INTERNAL_NAME = NS + "hasSourcePropertyInternalName";
  public static final String HAS_TRANSFORMATION_SCOPE = NS + "hasTransformationScope";
  public static final String HAS_TARGET_VALUE = NS + "hasTargetValue";

  public static final String EVENT_NAME = NS + "eventName";
  public static final String KEEP_BOTH = NS + "keepBoth";

  public static final String LIST_PROPERTY_NAME = NS + "listPropertyName";
  public static final String HAS_NAME = NS + "hasName";

  public static final String REPLACES_PROPERTY = NS + "replacesProperty";

  public static final String REPLACE_FROM = NS + "replaceFrom";
  public static final String REPLACE_TO = NS + "replaceTo";
  public static final String REPLACE_WITH = NS + "replaceWith";
  public static final String RENAMING_ALLOWED = NS + "renamingAllowed";
  public static final String TYPE_CAST_ALLOWED = NS + "typeCastAllowed";
  public static final String DOMAIN_PROPERTY_CAST_ALLOWED = NS + "domainPropertyCastAllowed";

  public static final String HAS_QUANTITY_VALUE = NS + "hasQuantityValue";
  public static final String MINIMUM_EVENT_PROPERTY_QUALITY = NS + "minimumEventPropertyQuality";
  public static final String MAXIMUM_EVENT_PROPERTY_QUALITY = NS + "maximumEventPropertyQuality";

  public static final String MINIMUM_EVENT_STREAM_QUALITY = NS + "minimumEventStreamQuality";
  public static final String MAXIMUM_EVENT_STREAM_QUALITY = NS + "maximumEventStreamQuality";

  public static final String HAS_CAPABILTIY = NS + "hasCapability";
  public static final String MEASURES_OBJECT = NS + "measuresObject";

  public static final String HAS_MEASUREMENT_PROPERTY_MIN_VALUE = NS + "hasMeasurementPropertyMinValue";
  public static final String HAS_MEASUREMENT_PROPERTY_MAX_VALUE = NS + "hasMeasurementPropertyMaxValue";

  public static final String HAS_RUNTIME_VALUE = NS + "hasRuntimeValue";

  public static final String HAS_RUNTIME_NAME = NS + "hasRuntimeName";
  public static final String REQUIRED = NS + "required";
  public static final String DOMAIN_PROPERTY = NS + "domainProperty";
  public static final String HAS_EVENT_PROPERTY_QUALITY_DEFINITION = NS + "hasEventPropertyQualityDefinition";
  public static final String HAS_EVENT_PROPERTY_QUALITY_REQUIREMENT = NS + "hasEventPropertyQualityRequirement";

  public static final String HAS_EVENT_PROPERTY = NS + "hasEventProperty";
  public static final String HAS_PROPERTY_TYPE = NS + "hasPropertyType";
  public static final String HAS_MEASUREMENT_UNIT = NS + "hasMeasurementUnit";
  public static final String HAS_VALUE_SPECIFICATION = NS + "hasValueSpecification";

  public static final String HAS_OPTION = NS + "hasOption";
  public static final String MEMBER = NS + "member";
  public static final String MEMBER_TYPE = NS + "memberType";

  public static final String REQUIRED_CLASS = NS + "requiredClass";
  public static final String HAS_SUPPORTED_PROPERTY = NS + "hasSupportedProperty";

  public static final String HAS_VALUE = NS + "hasValue";
  public static final String REQUIRED_DATATYPE = NS + "requiresDatatype";
  public static final String REQUIRED_DOMAIN_PROPERTY = NS + "requiresDomainProperty";
  public static final String MAPS_TO = NS + "mapsTo";
  public static final String MULTI_LINE = NS + "multiLine";
  public static final String HTML_ALLOWED = NS + "htmlAllowed";
  public static final String PLACEHOLDERS_SUPPORTED = NS + "placeholdersSupported";

  public static final String MAPS_FROM = NS + "mapsFrom";

  public static final String MATCH_LEFT = NS + "matchLeft";
  public static final String MATCH_RIGHT = NS + "matchRight";

  public static final String IS_SELECTED = NS + "isSelected";
  public static final String REMOTE_URL = NS + "remoteUrl";
  public static final String VALUE_FIELD_NAME = NS + "valueFieldName";
  public static final String LABEL_FIELD_NAME = NS + "labelFieldName";
  public static final String DESCRIPTION_FIELD_NAME = NS + "descriptionFieldName";

  public static final String INTERNAL_NAME = NS + "internalName";
  public static final String REQUIRES_PROPERTY = NS + "requiresProperty";

  public static final String APPLICATION_URL = NS + "applicationUrl";
  public static final String APPLICATION_LINK_TYPE = NS + "applicationLinkType";

  public static final String HAS_EVENT_STREAM_QUALITY_DEFINITION = NS + "hasEventStreamQualityDefinition";
  public static final String HAS_EVENT_STREAM_QUALITY_REQUIREMENT = NS + "hasEventStreamQualityRequirement";

  public static final String HAS_GROUNDING = NS + "hasGrounding";
  public static final String HAS_SCHEMA = NS + "hasSchema";

  public static final String HAS_MEASUREMENT_CAPABILTIY = NS + "hasMeasurementCapability";
  public static final String HAS_MEASUREMENT_OBJECT = NS + "hasMeasurementObject";

  public static final String HAS_PROPERTY_SCOPE = NS + "hasPropertyScope";

  public static final String HAS_WILDCARD_TOPIC_NAME = NS + "hasWildcardTopicName";
  public static final String HAS_WILDCARD_TOPIC_MAPPING = NS + "hasWildcardTopicMapping";
  public static final String HAS_ACTUAL_TOPIC_NAME = NS + "hasActualTopicName";

  public static final String HAS_TOPIC_PARAMETER_TYPE = NS + "hasTopicParameterType" ;
  public static final String HAS_TOPIC_MAPPING_ID = NS + "hasTopicMappingId";
  public static final String HAS_MAPPED_RUNTIME_NAME = NS + "hasMappedRuntimeName";

  public static final String HAS_LINKED_MAPPING_PROPERTY_ID = NS + "hasLinkedMappingPropertyId";
}
