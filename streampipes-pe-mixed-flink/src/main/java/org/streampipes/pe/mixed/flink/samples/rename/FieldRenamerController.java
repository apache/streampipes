package org.streampipes.pe.mixed.flink.samples.rename;

import org.streampipes.container.util.StandardTransportFormat;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.output.OutputStrategy;
import org.streampipes.model.output.ReplaceOutputStrategy;
import org.streampipes.model.output.UriPropertyMapping;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventPropertyPrimitive;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.pe.mixed.flink.samples.FlinkConfig;
import org.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.streampipes.wrapper.flink.FlinkDataProcessorRuntime;
import org.streampipes.wrapper.flink.FlinkDeploymentConfig;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FieldRenamerController extends FlinkDataProcessorDeclarer<FieldRenamerParameters> {

	@Override
	public DataProcessorDescription declareModel() {
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		EventProperty e1 = new EventPropertyPrimitive();
		eventProperties.add(e1);
		EventSchema schema1 = new EventSchema();
		schema1.setEventProperties(eventProperties);
		
		SpDataStream stream1 = new SpDataStream();
		stream1.setEventSchema(schema1);
		
		DataProcessorDescription desc = new DataProcessorDescription("rename", "Field Renamer", "Replaces the runtime name of an event property with a custom defined name. Useful for data ingestion purposes where a specific event schema is needed.");
		
		desc.addEventStream(stream1);
		
		List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();
		
		ReplaceOutputStrategy strategy = new ReplaceOutputStrategy();
		UriPropertyMapping mapping = new UriPropertyMapping();
		mapping.setReplaceFrom(URI.create(e1.getElementId()));
		mapping.setReplaceWith(new EventPropertyPrimitive());
		mapping.setRenamingAllowed(true);
		mapping.setTypeCastAllowed(false);
		mapping.setDomainPropertyCastAllowed(false);
		strategy.setReplaceProperties(Arrays.asList(mapping));
		
		strategies.add(strategy);
		desc.setOutputStrategies(strategies);
		desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());
		
		return desc;

	}

	@Override
	public FlinkDataProcessorRuntime<FieldRenamerParameters> getRuntime(
			DataProcessorInvocation graph) {
		ReplaceOutputStrategy ros = (ReplaceOutputStrategy) graph.getOutputStrategies().get(0);
		EventProperty oldProperty = SepaUtils.getEventPropertyById(graph, ros.getReplaceProperties().get(0).getReplaceTo());
		String newPropertyName = ros.getReplaceProperties().get(0).getReplaceWith().getRuntimeName();
		
		return new FieldRenamerProgram (
				new FieldRenamerParameters(graph, oldProperty.getRuntimeName(), newPropertyName),
				new FlinkDeploymentConfig(FlinkConfig.JAR_FILE,
						FlinkConfig.INSTANCE.getFlinkHost(), FlinkConfig.INSTANCE.getFlinkPort()));
	}

}
