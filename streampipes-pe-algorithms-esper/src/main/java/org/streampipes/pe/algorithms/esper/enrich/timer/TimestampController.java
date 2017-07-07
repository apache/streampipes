package org.streampipes.pe.algorithms.esper.enrich.timer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.streampipes.pe.algorithms.esper.config.EsperConfig;
import org.streampipes.model.impl.EpaType;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.Response;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.eventproperty.EventPropertyPrimitive;
import org.streampipes.model.impl.graph.SepaDescription;
import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.model.impl.output.AppendOutputStrategy;
import org.streampipes.model.impl.output.OutputStrategy;
import org.streampipes.model.impl.quality.EventStreamQualityRequirement;
import org.streampipes.model.impl.quality.Frequency;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.model.vocabulary.XSD;
import org.streampipes.runtime.flat.declarer.FlatEpDeclarer;
import org.streampipes.container.util.StandardTransportFormat;
import org.streampipes.commons.Utils;

public class TimestampController extends FlatEpDeclarer<TimestampParameter>{

	@Override
	public SepaDescription declareModel() {
				
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();		
		EventSchema schema1 = new EventSchema();
		schema1.setEventProperties(eventProperties);
		
		EventStream stream1 = new EventStream();
		stream1.setEventSchema(schema1);
		
		SepaDescription desc = new SepaDescription("enrich_timestamp", "Timestamp Enrichment", "Appends the current time in ms to the event payload");
		desc.setIconUrl(EsperConfig.iconBaseUrl + "/Timer_Icon_HQ.png");
		desc.setCategory(Arrays.asList(EpaType.ENRICH.name()));
		//TODO check if needed
		stream1.setUri(EsperConfig.serverUrl +"/" +desc.getElementId());
		
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
				"appendedTime", "", Utils.createURI("http://schema.org/Number")));
		
		outputStrategy.setEventProperties(appendProperties);
		strategies.add(outputStrategy);
		desc.setOutputStrategies(strategies);
		desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());
		
		return desc;
	}

	@Override
	public Response invokeRuntime(SepaInvocation sepa) {
		
		AppendOutputStrategy strategy = (AppendOutputStrategy) sepa.getOutputStrategies().get(0);

		String appendTimePropertyName = SepaUtils.getEventPropertyName(strategy.getEventProperties(), "appendedTime");
		
		List<String> selectProperties = new ArrayList<>();
		for(EventProperty p : sepa.getInputStreams().get(0).getEventSchema().getEventProperties())
		{
			selectProperties.add(p.getRuntimeName());
		}
		
		TimestampParameter staticParam = new TimestampParameter (
				sepa, 
				appendTimePropertyName,
				selectProperties);

		return submit(staticParam, TimestampEnrichment::new, sepa);

	}
}
