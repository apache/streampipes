package de.fzi.cep.sepa.esper.absence;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.desc.EpDeclarer;
import de.fzi.cep.sepa.esper.config.EsperConfig;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.Response;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.impl.output.CustomOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.model.impl.staticproperty.FreeTextStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;
import de.fzi.cep.sepa.model.util.SepaUtils;
import de.fzi.cep.sepa.util.StandardTransportFormat;

public class AbsenceController extends EpDeclarer<AbsenceParameters>{

	@Override
	public SepaDescription declareModel() {
		
		EventStream stream1 = new EventStream();
		EventStream stream2 = new EventStream();
		
		SepaDescription desc = new SepaDescription("sepa/absence", "Absence", "Detects whether an event does not arrive within a specified time after the occurrence of another event.");
		
		stream1.setUri(EsperConfig.serverUrl +"/" +Utils.getRandomString());
		stream2.setUri(EsperConfig.serverUrl +"/" +Utils.getRandomString());
		desc.addEventStream(stream1);
		desc.addEventStream(stream2);
		
		List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();
		strategies.add(new CustomOutputStrategy());
		desc.setOutputStrategies(strategies);
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
			
		staticProperties.add(new FreeTextStaticProperty("timeWindow", "Time window size (seconds)"));
		desc.setStaticProperties(staticProperties);
		desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());
		return desc;
	}

	@Override
	public Response invokeRuntime(SepaInvocation sepa) {
		
		List<String> selectProperties = new ArrayList<>();
		for (EventProperty p : sepa.getOutputStream().getEventSchema().getEventProperties()) {
			selectProperties.add(p.getRuntimeName());
		}
		
		int timeWindowSize = Integer.parseInt(
				((FreeTextStaticProperty) (SepaUtils.getStaticPropertyByName(sepa, "timeWindow"))).getValue());
		
		AbsenceParameters staticParam = new AbsenceParameters(sepa, selectProperties, timeWindowSize);
		
		try {
			invokeEPRuntime(staticParam, Absence::new, sepa);
			return new Response(sepa.getElementId(), true);
		} catch (Exception e) {
			e.printStackTrace();
			return new Response(sepa.getElementId(), false, e.getMessage());
		}
	}
}
