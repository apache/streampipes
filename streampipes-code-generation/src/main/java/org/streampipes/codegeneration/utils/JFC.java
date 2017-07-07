package org.streampipes.codegeneration.utils;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.squareup.javapoet.ClassName;

import org.streampipes.container.init.DeclarersSingleton;
import org.streampipes.container.util.StandardTransportFormat;
import org.streampipes.commons.config.ClientConfiguration;
import org.streampipes.wrapper.flink.AbstractFlinkAgentDeclarer;
import org.streampipes.wrapper.flink.AbstractFlinkConsumerDeclarer;
import org.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.streampipes.wrapper.flink.FlinkSecRuntime;
import org.streampipes.wrapper.flink.FlinkSepaRuntime;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.PrimitivePropertyBuilder;
import org.streampipes.sdk.stream.SchemaBuilder;
import org.streampipes.sdk.stream.StreamBuilder;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.graph.SecDescription;
import org.streampipes.model.impl.graph.SecInvocation;
import org.streampipes.model.impl.graph.SepaDescription;
import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.model.impl.output.AppendOutputStrategy;
import org.streampipes.model.impl.output.OutputStrategy;
import org.streampipes.model.util.SepaUtils;

/**
 * Java File Classes (JFC)
 * @author philipp
 *
 */
public abstract class JFC {

	public static ClassName MAP = ClassName.get(Map.class);
	public static ClassName LIST = ClassName.get(List.class);
	public static ClassName ARRAY_LIST = ClassName.get(ArrayList.class);

	public static ClassName STRING = ClassName.get("", "String");
	public static ClassName OVERRIDE = ClassName.get("", "Override");
	public static ClassName OBJECT = ClassName.get("", "Object");
	public static ClassName EXCEPTION = ClassName.get("", "Exception");

	public static ClassName DATA_STREAM = ClassName.get("org.apache.flink.streaming.api.datastream", "DataStream");
	public static ClassName DATA_STREAM_SINK = ClassName.get("org.apache.flink.streaming.api.datastream", "DataStreamSink");
	public static ClassName FLAT_MAP_FUNCTION = ClassName.get("org.apache.flink.api.common.functions", "FlatMapFunction");
	public static ClassName COLLECTOR = ClassName.get("org.apache.flink.util", "Collector");

	public static ClassName SEPA_DESCRIPTION = ClassName.get(SepaDescription.class);
	public static ClassName SEC_DESCRIPTION = ClassName.get(SecDescription.class);
	public static ClassName SEPA_INVOCATION = ClassName.get(SepaInvocation.class);
	public static ClassName SEC_INVOCATION = ClassName.get(SecInvocation.class);
	public static ClassName SEPA_UTILS = ClassName.get(SepaUtils.class);
	public static ClassName EVENT_STREAM = ClassName.get(EventStream.class);
	public static ClassName STREAM_BUILDER = ClassName.get(StreamBuilder.class);
	public static ClassName SCHEMA_BUILDER = ClassName.get(SchemaBuilder.class);
	public static ClassName EVENT_PROPERTY = ClassName.get(EventProperty.class);
	public static ClassName PRIMITIVE_PROPERTY_BUILDER = ClassName.get(PrimitivePropertyBuilder.class);
	public static ClassName APPEND_OUTPUT_STRATEGY = ClassName.get(AppendOutputStrategy.class);
	public static ClassName OUTPUT_STRATEGY = ClassName.get(OutputStrategy.class);
	public static ClassName EP_PROPERTIES = ClassName.get(EpProperties.class);
	public static ClassName STANDARD_TRANSPORT_FORMAT = ClassName.get(StandardTransportFormat.class);
//	public static ClassName EMBEDDED_MODEL_SUBMITTER = ClassName.get("EmbeddedModelSubmitter.class);
//	public static ClassName SEMANTIC_EVENT_PROCESSING_AGENT_DECLARER = ClassName.get(SemanticEventProcessingAgentDeclarer.class);
//	public static ClassName SEMANTIC_EVENT_PRODUCER_DECLARER = ClassName.get(SemanticEventProducerDeclarer.class);
//	public static ClassName SEMANTIC_EVENT_CONSUMER_DECLARER = ClassName.get(SemanticEventConsumerDeclarer.class);
	public static ClassName CLIENT_CONFIGURATION = ClassName.get(ClientConfiguration.class);

	public static ClassName CONTAINER_MODEL_SUBMITTER = ClassName.get("de.fzi.cep.sepa.client.container.init", "ContainerModelSubmitter");
	public static ClassName DECLARERS_SINGLETON = ClassName.get(DeclarersSingleton.class);



	public static ClassName FLINK_DEPLOYMENT_CONFIG = ClassName.get(FlinkDeploymentConfig.class);
	public static ClassName FLINK_SEPA_RUNTIME = ClassName.get(FlinkSepaRuntime.class);
	public static ClassName FLINK_SEC_RUNTIME = ClassName.get(FlinkSecRuntime.class);
	public static ClassName ABSTRACT_FLINK_AGENT_DECLARER = ClassName.get(AbstractFlinkAgentDeclarer.class);
	public static ClassName ABSTRACT_FLINK_CONSUMER_DECLARER = ClassName.get(AbstractFlinkConsumerDeclarer.class); 
}
