package de.fzi.cep.sepa.esper.aggregate.rate;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.esper.EsperDeclarer;
import de.fzi.cep.sepa.esper.config.EsperConfig;
import de.fzi.cep.sepa.esper.util.StandardTransportFormat;
import de.fzi.cep.sepa.model.impl.Domain;
import de.fzi.cep.sepa.model.impl.EventProperty;
import de.fzi.cep.sepa.model.impl.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.FreeTextStaticProperty;
import de.fzi.cep.sepa.model.impl.StaticProperty;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.impl.output.FixedOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.model.util.SepaUtils;
import de.fzi.cep.sepa.model.vocabulary.XSD;

public class EventRateController extends EsperDeclarer<EventRateParameter> {

	@Override
	public SepaDescription declareModel() {
		List<String> domains = new ArrayList<String>();
		domains.add(Domain.DOMAIN_PERSONAL_ASSISTANT.toString());
		domains.add(Domain.DOMAIN_PROASENSE.toString());
		
		
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();	
		
		EventSchema schema1 = new EventSchema();
		schema1.setEventProperties(eventProperties);
		
		EventStream stream1 = new EventStream();
		stream1.setEventSchema(schema1);
		
		SepaDescription desc = new SepaDescription("/sepa/eventrate", "Event rate", "Computes current event rate", "", "/sepa/eventrate", domains);
		
		//TODO check if needed
		stream1.setUri(EsperConfig.serverUrl +desc.getElementId());
		desc.addEventStream(stream1);
		desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());
		
		List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();
		
		
		EventProperty outputProperty = new EventPropertyPrimitive(XSD._double.toString(),
				"rate", "", de.fzi.cep.sepa.commons.Utils.createURI("http://schema.org/Number"));
		FixedOutputStrategy outputStrategy = new FixedOutputStrategy(Utils.createList(outputProperty));
		strategies.add(outputStrategy);
		desc.setOutputStrategies(strategies);
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		
		staticProperties.add(new FreeTextStaticProperty("rate", "average/sec"));
		staticProperties.add(new FreeTextStaticProperty("output", "output every (seconds)"));
		desc.setStaticProperties(staticProperties);
		
		return desc;
	}

	@Override
	public boolean invokeRuntime(SepaInvocation sepa) {
	
		String avgRate = ((FreeTextStaticProperty) (SepaUtils
				.getStaticPropertyByName(sepa, "rate"))).getValue();
		
		String outputRate = ((FreeTextStaticProperty) (SepaUtils
				.getStaticPropertyByName(sepa, "output"))).getValue();
	
		String topicPrefix = "topic://";
		EventRateParameter staticParam = new EventRateParameter(sepa, Integer.parseInt(avgRate), Integer.parseInt(outputRate), topicPrefix + sepa.getOutputStream().getEventGrounding().getTransportProtocol().getTopicName());
		
		
		try {
			return invokeEPRuntime(staticParam, EventRate::new, sepa);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}