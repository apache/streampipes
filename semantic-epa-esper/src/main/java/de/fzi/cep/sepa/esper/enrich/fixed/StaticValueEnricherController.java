package de.fzi.cep.sepa.esper.enrich.fixed;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.esper.config.EsperConfig;
import de.fzi.cep.sepa.model.impl.EpaType;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.Response;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.impl.output.AppendOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.model.impl.staticproperty.FreeTextStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;
import de.fzi.cep.sepa.model.util.SepaUtils;
import de.fzi.cep.sepa.model.vocabulary.MhWirth;
import de.fzi.cep.sepa.model.vocabulary.XSD;
import de.fzi.cep.sepa.runtime.flat.declarer.FlatEpDeclarer;
import de.fzi.cep.sepa.client.util.StandardTransportFormat;

public class StaticValueEnricherController extends FlatEpDeclarer<StaticValueEnricherParameters>{

	@Override
	public Response invokeRuntime(SepaInvocation sepa) {
		
		String value = SepaUtils.getFreeTextStaticPropertyValue(sepa, "value");
		
		StaticValueEnricherParameters staticParam = new StaticValueEnricherParameters(
				sepa, 
				"drillingStatus", value);
	
		try {
			invokeEPRuntime(staticParam, StaticValueEnricher::new, sepa);
			return new Response(sepa.getElementId(), true);
		} catch (Exception e) {
			e.printStackTrace();
			return new Response(sepa.getElementId(), false, e.getMessage());
		}
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
				"drillingStatus", "", de.fzi.cep.sepa.commons.Utils.createURI(MhWirth.DrillingStatus));;
	
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

