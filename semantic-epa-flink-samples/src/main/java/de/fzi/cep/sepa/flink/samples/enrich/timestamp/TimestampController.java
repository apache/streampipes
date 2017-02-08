package de.fzi.cep.sepa.flink.samples.enrich.timestamp;

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
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.impl.output.AppendOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.model.impl.quality.EventStreamQualityRequirement;
import de.fzi.cep.sepa.model.impl.quality.Frequency;
import de.fzi.cep.sepa.model.util.SepaUtils;
import de.fzi.cep.sepa.model.vocabulary.XSD;

public class TimestampController extends AbstractFlinkAgentDeclarer<TimestampParameters>{

	@Override
	public SepaDescription declareModel() {
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();		
		EventSchema schema1 = new EventSchema();
		schema1.setEventProperties(eventProperties);
		
		EventStream stream1 = new EventStream();
		stream1.setEventSchema(schema1);
		
		SepaDescription desc = new SepaDescription("enrich_timestamp", "Flink Timestamp Enrichment", "Appends the current time in ms to the event payload using Flink");
		
		List<EventStreamQualityRequirement> eventStreamQualities = new ArrayList<EventStreamQualityRequirement>();
		Frequency minFrequency = new Frequency(1);
		Frequency maxFrequency = new Frequency(100);
		eventStreamQualities.add(new EventStreamQualityRequirement(minFrequency, maxFrequency));
		stream1.setRequiresEventStreamQualities(eventStreamQualities);

		desc.addEventStream(stream1);
		
		List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();
		AppendOutputStrategy outputStrategy = new AppendOutputStrategy();

		List<EventProperty> appendProperties = new ArrayList<EventProperty>();
		appendProperties.add(new EventPropertyPrimitive(XSD._long.toString(),
				"appendedTime", "", de.fzi.cep.sepa.commons.Utils.createURI("http://schema.org/Number")));
		
		outputStrategy.setEventProperties(appendProperties);
		strategies.add(outputStrategy);
		desc.setOutputStrategies(strategies);
		desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());
		
		return desc;
	}

	@Override
	protected FlinkSepaRuntime<TimestampParameters> getRuntime(
			SepaInvocation graph) {
		System.out.println(Config.JAR_FILE);
		AppendOutputStrategy strategy = (AppendOutputStrategy) graph.getOutputStrategies().get(0);

		String appendTimePropertyName = SepaUtils.getEventPropertyName(strategy.getEventProperties(), "appendedTime");
		
		List<String> selectProperties = new ArrayList<>();
		for(EventProperty p : graph.getInputStreams().get(0).getEventSchema().getEventProperties())
		{
			selectProperties.add(p.getRuntimeName());
		}
		
		TimestampParameters staticParam = new TimestampParameters (
				graph, 
				appendTimePropertyName,
				selectProperties);
		
		return new TimestampProgram(staticParam, new FlinkDeploymentConfig(Config.JAR_FILE, Config.FLINK_HOST, Config.FLINK_PORT));
//		return new TimestampProgram(staticParam);
	}

}
