package org.streampipes.wrapper.flink.samples.rename;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.streampipes.wrapper.flink.AbstractFlinkAgentDeclarer;
import org.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.streampipes.wrapper.flink.FlinkSepaRuntime;
import org.streampipes.wrapper.flink.samples.FlinkConfig;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.eventproperty.EventPropertyPrimitive;
import org.streampipes.model.impl.graph.SepaDescription;
import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.model.impl.output.OutputStrategy;
import org.streampipes.model.impl.output.ReplaceOutputStrategy;
import org.streampipes.model.impl.output.UriPropertyMapping;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.container.util.StandardTransportFormat;

public class FieldRenamerController extends AbstractFlinkAgentDeclarer<FieldRenamerParameters>{

	@Override
	public SepaDescription declareModel() {
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		EventProperty e1 = new EventPropertyPrimitive();
		eventProperties.add(e1);
		EventSchema schema1 = new EventSchema();
		schema1.setEventProperties(eventProperties);
		
		EventStream stream1 = new EventStream();
		stream1.setEventSchema(schema1);
		
		SepaDescription desc = new SepaDescription("rename", "Field Renamer", "Replaces the runtime name of an event property with a custom defined name. Useful for data ingestion purposes where a specific event schema is needed.");
		
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
	protected FlinkSepaRuntime<FieldRenamerParameters> getRuntime(
			SepaInvocation graph) {
		ReplaceOutputStrategy ros = (ReplaceOutputStrategy) graph.getOutputStrategies().get(0);
		EventProperty oldProperty = SepaUtils.getEventPropertyById(graph, ros.getReplaceProperties().get(0).getReplaceTo());
		String newPropertyName = ros.getReplaceProperties().get(0).getReplaceWith().getRuntimeName();
		
		return new FieldRenamerProgram (
				new FieldRenamerParameters(graph, oldProperty.getRuntimeName(), newPropertyName),
				new FlinkDeploymentConfig(FlinkConfig.JAR_FILE, FlinkConfig.FLINK_HOST, FlinkConfig.FLINK_PORT));
	}

}
