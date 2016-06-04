package de.fzi.cep.sepa.flink.samples.hasher;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.client.util.StandardTransportFormat;
import de.fzi.cep.sepa.flink.AbstractFlinkAgentDeclarer;
import de.fzi.cep.sepa.flink.FlinkDeploymentConfig;
import de.fzi.cep.sepa.flink.FlinkSepaRuntime;
import de.fzi.cep.sepa.flink.samples.Config;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.model.impl.output.RenameOutputStrategy;
import de.fzi.cep.sepa.model.impl.staticproperty.MappingPropertyUnary;
import de.fzi.cep.sepa.model.impl.staticproperty.OneOfStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.Option;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;
import de.fzi.cep.sepa.model.util.SepaUtils;

public class FieldHasherController extends AbstractFlinkAgentDeclarer<FieldHasherParameters>{

	@Override
	public SepaDescription declareModel() {
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();		
		EventSchema schema1 = new EventSchema();
		schema1.setEventProperties(eventProperties);
		
		EventStream stream1 = new EventStream();
		stream1.setEventSchema(schema1);
		
		SepaDescription desc = new SepaDescription("fieldhasher", "Field Hasher", "The Field Hasher uses an algorithm to encode values in a field. The Field Hasher can use MD5, SHA1 or SHA2 to hash field values.");
		
		desc.addEventStream(stream1);
		
		List<StaticProperty> staticProperties = new ArrayList<>();
		staticProperties.add(new MappingPropertyUnary("property-mapping", "Field", "The field the hash function should be applied on"));
		OneOfStaticProperty algorithmSelection = new OneOfStaticProperty("hash-algorithm", "Hash Algorithm", "The hash algorithm that should be used.");
		algorithmSelection.addOption(new Option("SHA1"));
		algorithmSelection.addOption(new Option("SHA2"));
		algorithmSelection.addOption(new Option("MD5"));
		
		desc.setStaticProperties(staticProperties);
		
		List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();
		RenameOutputStrategy keepOutput = new RenameOutputStrategy();
		
		strategies.add(keepOutput);
		desc.setOutputStrategies(strategies);
		desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());
		
		return desc;
	}

	@Override
	protected FlinkSepaRuntime<FieldHasherParameters> getRuntime(
			SepaInvocation graph) {
		String propertyName = SepaUtils.getMappingPropertyName(graph, "property-mapping");
		
		HashAlgorithmType hashAlgorithmType = HashAlgorithmType.valueOf(SepaUtils.getOneOfProperty(graph, "hash-algorithm"));
		
		return new FieldHasherProgram(
				new FieldHasherParameters(graph, propertyName, hashAlgorithmType),
				new FlinkDeploymentConfig(Config.JAR_FILE, Config.FLINK_HOST, Config.FLINK_PORT));
	}

}
