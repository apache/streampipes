package de.fzi.cep.sepa.streampipes.codegeneration.utils;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.util.Collector;

import com.squareup.javapoet.ClassName;

import de.fzi.cep.sepa.desc.EmbeddedModelSubmitter;
import de.fzi.cep.sepa.desc.declarer.SemanticEventConsumerDeclarer;
import de.fzi.cep.sepa.desc.declarer.SemanticEventProcessingAgentDeclarer;
import de.fzi.cep.sepa.desc.declarer.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.flink.AbstractFlinkAgentDeclarer;
import de.fzi.cep.sepa.flink.FlinkDeploymentConfig;
import de.fzi.cep.sepa.flink.FlinkSepaRuntime;
import de.fzi.cep.sepa.model.builder.EpProperties;
import de.fzi.cep.sepa.model.builder.PrimitivePropertyBuilder;
import de.fzi.cep.sepa.model.builder.SchemaBuilder;
import de.fzi.cep.sepa.model.builder.StreamBuilder;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.output.AppendOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.util.StandardTransportFormat;

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

	public static ClassName DATA_STREAM = ClassName.get(DataStream.class);
	public static ClassName FLAT_MAP_FUNCTION = ClassName.get(FlatMapFunction.class);
	public static ClassName COLLECTOR = ClassName.get(Collector.class);
	
	
	
	public static ClassName SEPA_DESCRIPTION = ClassName.get(SepaDescription.class);
	public static ClassName EVENT_STREAM = ClassName.get(EventStream.class);
	public static ClassName STREAM_BUILDER = ClassName.get(StreamBuilder.class);
	public static ClassName SCHEMA_BUILDER = ClassName.get(SchemaBuilder.class);
	public static ClassName EVENT_PROPERTY = ClassName.get(EventProperty.class);
	public static ClassName PRIMITIVE_PROPERTY_BUILDER = ClassName.get(PrimitivePropertyBuilder.class);
	public static ClassName APPEND_OUTPUT_STRATEGY = ClassName.get(AppendOutputStrategy.class);
	public static ClassName OUTPUT_STRATEGY = ClassName.get(OutputStrategy.class);
	public static ClassName EP_PROPERTIES = ClassName.get(EpProperties.class);
	public static ClassName STANDARD_TRANSPORT_FORMAT = ClassName.get(StandardTransportFormat.class);
	public static ClassName EMBEDDED_MODEL_SUBMITTER = ClassName.get(EmbeddedModelSubmitter.class);
	public static ClassName SEMANTIC_EVENT_PROCESSING_AGENT_DECLARER = ClassName.get(SemanticEventProcessingAgentDeclarer.class);
	public static ClassName SEMANTIC_EVENT_PRODUCER_DECLARER = ClassName.get(SemanticEventProducerDeclarer.class);
	public static ClassName SEMANTIC_EVENT_CONSUMER_DECLARER = ClassName.get(SemanticEventConsumerDeclarer.class);



	public static ClassName FLINK_DEPLOYMENT_CONFIG = ClassName.get(FlinkDeploymentConfig.class);
	public static ClassName FLINK_SEPA_RUNTIME = ClassName.get(FlinkSepaRuntime.class);
	public static ClassName ABSTRACT_FLINK_AGENT_DECLARER = ClassName.get(AbstractFlinkAgentDeclarer.class);
}
