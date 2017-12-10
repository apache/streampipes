package org.streampipes.codegeneration.utils;


import com.squareup.javapoet.ClassName;
import org.streampipes.container.init.DeclarersSingleton;
import org.streampipes.container.util.StandardTransportFormat;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataSinkDescription;
import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.output.AppendOutputStrategy;
import org.streampipes.model.output.OutputStrategy;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.sdk.PrimitivePropertyBuilder;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.stream.SchemaBuilder;
import org.streampipes.sdk.stream.StreamBuilder;
import org.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.streampipes.wrapper.flink.FlinkDataSinkDeclarer;
import org.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.streampipes.wrapper.flink.FlinkDataSinkRuntime;
import org.streampipes.wrapper.flink.FlinkDataProcessorRuntime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

	public static ClassName SEPA_DESCRIPTION = ClassName.get(DataProcessorDescription.class);
	public static ClassName SEC_DESCRIPTION = ClassName.get(DataSinkDescription.class);
	public static ClassName SEPA_INVOCATION = ClassName.get(DataProcessorInvocation.class);
	public static ClassName SEC_INVOCATION = ClassName.get(DataSinkInvocation.class);
	public static ClassName SEPA_UTILS = ClassName.get(SepaUtils.class);
	public static ClassName EVENT_STREAM = ClassName.get(SpDataStream.class);
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

	public static ClassName CONTAINER_MODEL_SUBMITTER = ClassName.get("de.fzi.cep.sepa.client.container.init", "ContainerModelSubmitter");
	public static ClassName DECLARERS_SINGLETON = ClassName.get(DeclarersSingleton.class);



	public static ClassName FLINK_DEPLOYMENT_CONFIG = ClassName.get(FlinkDeploymentConfig.class);
	public static ClassName FLINK_SEPA_RUNTIME = ClassName.get(FlinkDataProcessorRuntime.class);
	public static ClassName FLINK_SEC_RUNTIME = ClassName.get(FlinkDataSinkRuntime.class);
	public static ClassName ABSTRACT_FLINK_AGENT_DECLARER = ClassName.get(FlinkDataProcessorDeclarer.class);
	public static ClassName ABSTRACT_FLINK_CONSUMER_DECLARER = ClassName.get(FlinkDataSinkDeclarer.class);
}
