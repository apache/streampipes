package org.streampipes.vocabulary;

public enum StreamPipes {

  ENUMERATION_VALUE_SPECIFICATION("EnumerationValueSpecification"),
  QUANTITATIVE_VALUE_SPECIFICATION("QuantitativeValueSpecification"),
  VALUE_SPECIFICATION("ValueSpecification"),

  EVENT_PROPERTY("EventProperty"),
  EVENT_PROPERTY_LIST("EventPropertyList"),
  EVENT_PROPERTY_NESTED("EventPropertyNested"),
  EVENT_PROPERTY_PRIMITIVE("EventPropertyPrimitive"),

  DATA_SINK_DESCRIPTION_GRAPH("DataSinkDescriptionGraph"),
  DATA_SINK_INVOCATION_GRAPH("DataSinkInvocationGraph"),

  DATA_PROCESSOR_DESCRIPTION_GRAPH("DataProcessorDescriptionGraph"),
  DATA_PROCESSOR_INVOCATION_GRAPH("DataProcessorInvocationGraph"),

  DATA_SOURCE_DESCRIPTION_GRAPH("DataSourceDescriptionGraph"),

  APPEND_OUTPUT_STRATEGY("AppendOutputStrategy"),
  CUSTOM_OUTPUT_STRATEGY("CustomOutputStrategy"),
  FIXED_OUTPUT_STRATEGY("FixedOutputStrategy"),
  LIST_OUTPUT_STRATEGY("ListOutputStrategy"),
  OUTPUT_STRATEGY("OutputStrategy"),
  KEEP_OUTPUT_STRATEGY("KeepOutputStrategy"),
  REPLACE_OUTPUT_STRATEGY("ReplaceOutputStrategy"),

  URI_PROPERTY_MAPPING("UriPropertyMapping"),

  EVENT_PROPERTY_QUALITY_DEFINITION("EventPropertyQualityDefinition"),
  EVENT_PROPERTY_QUALITY_REQUIREMENT("EventPropertyQualityRequirement"),
  EVENT_STREAM_QUALITY_DEFINITION("EventStreamQualityDefinition"),
  Event_STREAM_QUALITY_REQUIREMENT("EventStreamQualityRequirement"),

  MEASUREMENT_CAPABILITY("MeasurementCapability"),
  MEASUREMENT_OBJECT("MeasurementObject"),

  ANY_STATIC_PROPERTY("AnyStaticProperty"),
  COLLECTION_STATIC_PROPERTY("CollectionStaticProperty"),
  DOMAIN_STATIC_PROPERTY("DomainStaticProperty"),
  FREE_TEXT_STATIC_PROPERTY("FreeTextStaticProperty"),
  MAPPING_PROPERTY("MappingProperty"),
  MAPPING_PROPERTY_UNARY("MappingPropertyUnary"),
  MAPPING_PROPERTY_NARY("MappingPropertyNary"),
  MATCHING_STATIC_PROPERTY("MatchingStaticProperty"),
  ONE_OF_STATIC_PROPERTY("OneOfStaticProperty"),
  OPTION("Option"),
  PROPERTY_VALUE_SPECIFICATION("PropertyValueSpecification"),
  REMOTE_ONE_OF_STATIC_PROPERTY("RemoteOneOfStaticProperty"),
  STATIC_PROPERTY("StaticProperty"),
  SUPPORTED_PROPERTY("SupportedProperty"),

  APPLICATION_LINK("ApplicationLink"),
  ELEMENT_STATUS_INFO_SETTINGS("ElementStatusInfoSettings"),

  EVENT_GROUNDING("EventGrounding"),
  EVENT_SCHEMA("EventSchema"),
  EVENT_SOURCE("EventSource"),
  EVENT_STREAM("EventStream"),

  JMS_TRANSPORT_PROTOCOL("JmsTransportProtocol"),
  KAFKA_TRANSPORT_PROTOCOL("KafkaTransportProtocol"),
  TRANSPORT_FORMAT("TransportFormat"),
  TRANSPORT_PROTOCOL("TransportProtocol");

  private final String NS = "https://streampipes.org/vocabulary/v1/";
  private final String NS_PREFIX = "sp";

  private String entityName;

  StreamPipes(String entityName) {
    this.entityName = entityName;
  }

  public String uri() {
    return NS + entityName;
  }

  public String prefixedUri() {
    return NS_PREFIX +":" +entityName;
  }



}
