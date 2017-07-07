package org.streampipes.pe.algorithms.esper.enrich.fixed;

import java.util.ArrayList;
import java.util.List;

import org.streampipes.pe.algorithms.esper.EsperEventEngine;
import org.streampipes.model.impl.eventproperty.EventProperty;

public class StaticValueEnricher extends EsperEventEngine<StaticValueEnricherParameters>{

	@Override
	protected List<String> statements(StaticValueEnricherParameters bindingParameters) {
		/*
		 * select * from pattern[every RPM(rpm > threshold) -> Torque(torque > threshold) where timer:within(10 secs)
		 */
		bindingParameters.getOutputProperties().forEach(property -> System.out.println(property));
		List<String> statements = new ArrayList<>();
		String eventInName = fixEventName(bindingParameters.getInputStreamParams().get(0).getInName());
		String selectClause = makeSelectClause(bindingParameters.getGraph().getInputStreams().get(0).getEventSchema().getEventProperties(), bindingParameters.getAppendPropertyName(), bindingParameters.getValue());
		String pattern = selectClause +" from " +eventInName;
				
		System.out.println(pattern);
		statements.add(pattern);
		return statements;
	}
	
	private String makeSelectClause(List<EventProperty> eventProperties, String propertyName, String propertyValue)
	{
		String selectClause = "select ";
		for(EventProperty p : eventProperties) selectClause = selectClause +p.getRuntimeName() +", ";
		selectClause = selectClause +"'" +propertyValue +"' as " +propertyName +" ";
		
		return selectClause;
		
	}

}
