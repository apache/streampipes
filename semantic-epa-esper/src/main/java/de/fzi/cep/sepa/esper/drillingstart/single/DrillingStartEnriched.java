package de.fzi.cep.sepa.esper.drillingstart.single;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.esper.EsperEventEngine;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;

public class DrillingStartEnriched extends EsperEventEngine<DrillingStartEnrichedParameters>{

	@Override
	protected List<String> statements(DrillingStartEnrichedParameters bindingParameters) {
		/*
		 * select * from pattern[every RPM(rpm > threshold) -> Torque(torque > threshold) where timer:within(10 secs)
		 */
		bindingParameters.getOutputProperties().forEach(property -> System.out.println(property));
		List<String> statements = new ArrayList<>();
		String eventInName = fixEventName(bindingParameters.getInputStreamParams().get(0).getInName());
		String selectClause = makeSelectClause(bindingParameters.getGraph().getInputStreams().get(0).getEventSchema().getEventProperties());
//		String pattern = selectClause +" from pattern[every ((s1=" +eventInName 
//				+"(" 
//				+bindingParameters.getRpmPropertyName() 
//				+"<=" 
//				+bindingParameters.getMinRpm() 
//				+", "
//				+bindingParameters.getTorquePropertyName()
//				+"<="
//				+bindingParameters.getMinTorque()
//				+"))"
//				
//				+"-> (s2=" 
//				+eventInName 
//				+"(" 
//				+bindingParameters.getRpmPropertyName() 
//				+">" 
//				+bindingParameters.getMinRpm() 
//				+", " 
//				+bindingParameters.getTorquePropertyName() 
//				+" > " 
//				+bindingParameters.getMinTorque() 
//				+")))"
//				+"]";
		
		String pattern = selectClause + " from pattern [every (s1=" +eventInName +" -> s2=" +eventInName +")] ";
		pattern += " where (s2." +bindingParameters.getTorquePropertyName() +" > 5 and s2." +bindingParameters.getRpmPropertyName() +" > 5) and ";
		pattern += "(s1." +bindingParameters.getTorquePropertyName() +" <= 5 or s1." +bindingParameters.getRpmPropertyName() +" <= 5) and ";
		pattern +="((s1." +bindingParameters.getTorquePropertyName() +" != s2." +bindingParameters.getTorquePropertyName() +") and (s1." +bindingParameters.getRpmPropertyName() +" != s2." +bindingParameters.getRpmPropertyName() +"))";
		
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
