package de.fzi.cep.sepa.esper.aggregate.count;

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
import de.fzi.cep.sepa.model.impl.MappingProperty;
import de.fzi.cep.sepa.model.impl.MappingPropertyNary;
import de.fzi.cep.sepa.model.impl.OneOfStaticProperty;
import de.fzi.cep.sepa.model.impl.Option;
import de.fzi.cep.sepa.model.impl.StaticProperty;
import de.fzi.cep.sepa.model.impl.graph.SEPA;
import de.fzi.cep.sepa.model.impl.graph.SEPAInvocationGraph;
import de.fzi.cep.sepa.model.impl.output.AppendOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.model.util.SEPAUtils;
import de.fzi.cep.sepa.model.vocabulary.XSD;

public class CountController extends EsperDeclarer<CountParameter>{

	@Override
	public SEPA declareModel() {
		List<String> domains = new ArrayList<String>();
		domains.add(Domain.DOMAIN_PERSONAL_ASSISTANT.toString());
		domains.add(Domain.DOMAIN_PROASENSE.toString());
	
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		EventSchema schema1 = new EventSchema();
		schema1.setEventProperties(eventProperties);
		
		EventStream stream1 = new EventStream();
		stream1.setEventSchema(schema1);
		
		SEPA desc = new SEPA("/sepa/count", "Count Aggregation", "Performs an aggregation based on a given event property and outputs the number of occurrences.", "", "/sepa/count", domains);
		desc.setIconUrl(EsperConfig.iconBaseUrl + "/Counter_Icon_HQ.png");
		
		//TODO check if needed
		stream1.setUri(EsperConfig.serverUrl +desc.getElementId());
		desc.addEventStream(stream1);
		
		List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();
		
		EventProperty outputProperty = new EventPropertyPrimitive(XSD._integer.toString(),
				"countValue", "", de.fzi.cep.sepa.commons.Utils.createURI("http://schema.org/Number"));
		AppendOutputStrategy outputStrategy = new AppendOutputStrategy(Utils.createList(outputProperty));
		strategies.add(outputStrategy);
		desc.setOutputStrategies(strategies);
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		
		OneOfStaticProperty operation = new OneOfStaticProperty("scale", "Scale: ");
		operation.addOption(new Option("Hours"));
		operation.addOption(new Option("Minutes"));
		operation.addOption(new Option("Seconds"));
		
		staticProperties.add(operation);
		
		MappingProperty mp = new MappingPropertyNary("groupBy", "Group stream by: ");
		staticProperties.add(mp);
		staticProperties.add(new FreeTextStaticProperty("timeWindow", "Time window size: "));
		desc.setStaticProperties(staticProperties);
		desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());
		
		return desc;
	}

	@Override
	public boolean invokeRuntime(SEPAInvocationGraph sepa) {		
		
		List<String> groupBy = SEPAUtils.getMultipleMappingPropertyNames(sepa,
				"groupBy", true);
		
		int timeWindowSize = Integer.parseInt(((FreeTextStaticProperty) (SEPAUtils
				.getStaticPropertyByName(sepa, "timeWindow"))).getValue());
	
		String scale = SEPAUtils.getOneOfProperty(sepa,
				"scale");
		
		TimeScale timeScale;
		
		if (scale.equals("Hours")) timeScale = TimeScale.HOURS;
		else if (scale.equals("Minutes")) timeScale = TimeScale.MINUTES;
		else timeScale = TimeScale.SECONDS;
		
		List<String> selectProperties = new ArrayList<>();
		for(EventProperty p : sepa.getInputStreams().get(0).getEventSchema().getEventProperties())
		{
			selectProperties.add(p.getRuntimeName());
		}
		
		CountParameter staticParam = new CountParameter(sepa, timeWindowSize, groupBy, timeScale, selectProperties);
		
		try {
			return invokeEPRuntime(staticParam, Count::new, sepa);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
