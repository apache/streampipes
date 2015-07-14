package de.fzi.cep.sepa.esper.drillingstop.single;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.esper.EsperEventEngine;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;

public class DrillingStopEnriched extends EsperEventEngine<DrillingStopEnrichedParameters>{

	@Override
	protected List<String> statements(DrillingStopEnrichedParameters bindingParameters) {
		/*
		 * select * from pattern[every RPM(rpm > threshold) -> Torque(torque > threshold) where timer:within(10 secs)
		 */
		bindingParameters.getOutputProperties().forEach(property -> System.out.println(property));
		List<String> statements = new ArrayList<>();
		String eventInName = fixEventName(bindingParameters.getInputStreamParams().get(0).getInName());
		String selectClause = makeSelectClause(bindingParameters.getGraph().getInputStreams().get(0).getEventSchema().getEventProperties());
		String pattern = selectClause +" from pattern[every ((s1=" +eventInName 
				+"(" 
				+bindingParameters.getRpmPropertyName() 
				+">" 
				+bindingParameters.getMinRpm() 
				+", "
				+bindingParameters.getTorquePropertyName()
				+">"
				+bindingParameters.getMinTorque()
				+"))"
				
				+"-> (s2=" 
				+eventInName 
				+"(" 
				+bindingParameters.getRpmPropertyName() 
				+"<=" 
				+bindingParameters.getMinRpm() 
				+", " 
				+bindingParameters.getTorquePropertyName() 
				+" <= " 
				+bindingParameters.getMinTorque() 
				+")))"
				+"]";
		
		System.out.println(pattern);
		statements.add(pattern);
		return statements;
	}
	
	private String makeSelectClause(List<EventProperty> eventProperties)
	{
		String selectClause = "select '1' as drillingStatus, ";
		for(int i = 0; i < eventProperties.size(); i++)
		{
			EventProperty p = eventProperties.get(i);
			selectClause = selectClause + "s2." +p.getRuntimeName() +" as " +p.getRuntimeName();
			if (i < (eventProperties.size() - 1)) selectClause += ", ";
		}
		return selectClause;
		
	}

}
