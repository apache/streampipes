package org.streampipes.pe.mixed.flink.samples.hasher;

import org.streampipes.container.util.StandardTransportFormat;
import org.streampipes.wrapper.flink.AbstractFlinkAgentDeclarer;
import org.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.streampipes.wrapper.flink.FlinkSepaRuntime;
import org.streampipes.pe.mixed.flink.samples.FlinkConfig;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.output.OutputStrategy;
import org.streampipes.model.output.KeepOutputStrategy;
import org.streampipes.model.staticproperty.MappingPropertyUnary;
import org.streampipes.model.staticproperty.OneOfStaticProperty;
import org.streampipes.model.staticproperty.Option;
import org.streampipes.model.staticproperty.StaticProperty;
import org.streampipes.model.util.SepaUtils;

import java.util.ArrayList;
import java.util.List;

public class FieldHasherController extends AbstractFlinkAgentDeclarer<FieldHasherParameters> {

	@Override
	public DataProcessorDescription declareModel() {
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();		
		EventSchema schema1 = new EventSchema();
		schema1.setEventProperties(eventProperties);
		
		SpDataStream stream1 = new SpDataStream();
		stream1.setEventSchema(schema1);
		
		DataProcessorDescription desc = new DataProcessorDescription("fieldhasher", "Field Hasher", "The Field Hasher uses an algorithm to encode values in a field. The Field Hasher can use MD5, SHA1 or SHA2 to hash field values.");
		desc.setIconUrl(FlinkConfig.getIconUrl("field-hasher-icon"));
		desc.addEventStream(stream1);
		
		List<StaticProperty> staticProperties = new ArrayList<>();
		staticProperties.add(new MappingPropertyUnary("property-mapping", "Field", "The field the hash function should be applied on"));
		OneOfStaticProperty algorithmSelection = new OneOfStaticProperty("hash-algorithm", "Hash Algorithm", "The hash algorithm that should be used.");
		algorithmSelection.addOption(new Option("SHA1"));
		algorithmSelection.addOption(new Option("SHA2"));
		algorithmSelection.addOption(new Option("MD5"));
		staticProperties.add(algorithmSelection);
		desc.setStaticProperties(staticProperties);
		
		List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();
		KeepOutputStrategy keepOutput = new KeepOutputStrategy();
		
		strategies.add(keepOutput);
		desc.setOutputStrategies(strategies);
		desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());
		
		return desc;
	}

	@Override
	protected FlinkSepaRuntime<FieldHasherParameters> getRuntime(
			DataProcessorInvocation graph) {
		String propertyName = SepaUtils.getMappingPropertyName(graph, "property-mapping");
		
		HashAlgorithmType hashAlgorithmType = HashAlgorithmType.valueOf(SepaUtils.getOneOfProperty(graph, "hash-algorithm"));
		
		return new FieldHasherProgram(
				new FieldHasherParameters(graph, propertyName, hashAlgorithmType),
				new FlinkDeploymentConfig(FlinkConfig.JAR_FILE,
						FlinkConfig.INSTANCE.getFlinkHost(), FlinkConfig.INSTANCE.getFlinkPort()));
	}

}
