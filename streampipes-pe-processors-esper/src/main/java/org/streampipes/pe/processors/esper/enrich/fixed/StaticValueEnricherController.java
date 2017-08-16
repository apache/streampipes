package org.streampipes.pe.processors.esper.enrich.fixed;

import org.streampipes.commons.Utils;
import org.streampipes.container.util.StandardTransportFormat;
import org.streampipes.model.impl.EpaType;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.eventproperty.EventPropertyPrimitive;
import org.streampipes.model.impl.graph.SepaDescription;
import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.model.impl.output.AppendOutputStrategy;
import org.streampipes.model.impl.output.OutputStrategy;
import org.streampipes.model.impl.staticproperty.FreeTextStaticProperty;
import org.streampipes.model.impl.staticproperty.StaticProperty;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.model.vocabulary.MhWirth;
import org.streampipes.model.vocabulary.XSD;
import org.streampipes.pe.processors.esper.config.EsperConfig;
import org.streampipes.wrapper.ConfiguredEventProcessor;
import org.streampipes.wrapper.runtime.EventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessorDeclarerSingleton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StaticValueEnricherController extends StandaloneEventProcessorDeclarerSingleton<StaticValueEnricherParameters> {


	@Override
	public ConfiguredEventProcessor<StaticValueEnricherParameters, EventProcessor<StaticValueEnricherParameters>>
	onInvocation(SepaInvocation sepa) {
		String value = SepaUtils.getFreeTextStaticPropertyValue(sepa, "value");

		StaticValueEnricherParameters staticParam = new StaticValueEnricherParameters(
						sepa,
						"drillingStatus", value);

		return new ConfiguredEventProcessor<>(staticParam, StaticValueEnricher::new);
	}

	@Override
	public SepaDescription declareModel() {
	
		EventStream stream1 = new EventStream();
		
		EventSchema schema1 = new EventSchema();
		EventPropertyPrimitive p1 = new EventPropertyPrimitive(Utils.createURI(MhWirth.Rpm));
		schema1.addEventProperty(p1);
		
		EventPropertyPrimitive p2 = new EventPropertyPrimitive(Utils.createURI(MhWirth.Torque));
		schema1.addEventProperty(p2);
		
		
		SepaDescription desc = new SepaDescription("staticValueEnricher", "Static value enricher", "Static Value Enrichment");
		desc.setCategory(Arrays.asList(EpaType.ENRICH.name()));
		stream1.setUri(EsperConfig.serverUrl +"/" +Utils.getRandomString());
		stream1.setEventSchema(schema1);
		desc.addEventStream(stream1);
		
		List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();
		List<EventProperty> appendProperties = new ArrayList<EventProperty>();			
		
		EventProperty result = new EventPropertyPrimitive(XSD._string.toString(),
				"drillingStatus", "", Utils.createURI(MhWirth.DrillingStatus));;
	
		appendProperties.add(result);
		strategies.add(new AppendOutputStrategy(appendProperties));
		desc.setOutputStrategies(strategies);
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		
		FreeTextStaticProperty rpmThreshold = new FreeTextStaticProperty("value", "Provide static value", "");
		staticProperties.add(rpmThreshold);
		
		desc.setStaticProperties(staticProperties);
		desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());
		return desc;
	}
	
}

