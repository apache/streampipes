package org.streampipes.pe.processors.esper.compose;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.streampipes.pe.processors.esper.EsperEventEngine;
import org.streampipes.model.impl.eventproperty.EventProperty;

public class Compose extends EsperEventEngine<ComposeParameters> {

	@Override
	protected List<String> statements(ComposeParameters bindingParameters) {
		List<String> statements = new ArrayList<>();
		
		String inEventName1 = fixEventName(bindingParameters.getInputStreamParams().get(0).getInName());
		String inEventName2 = fixEventName(bindingParameters.getInputStreamParams().get(1).getInName());
		
		String selectProperties = getSelect(bindingParameters.getGraph().getInputStreams().get(0).getEventSchema().getEventProperties(),
				bindingParameters.getGraph().getInputStreams().get(1).getEventSchema().getEventProperties(),
				bindingParameters.getGraph().getOutputStream().getEventSchema().getEventProperties());
		
		String composeStatement = "select " +selectProperties +" from " +inEventName1 +".std:lastevent() as e1, " +inEventName2 +".std:lastevent() as e2";
		statements.add(composeStatement);
		
		return statements;
	}
	
	private String getSelect(List<EventProperty> e1Properties, List<EventProperty> e2Properties, List<EventProperty> outputProperties)
	{
		List<SelectProperty> selectProperties = new ArrayList<>();
		outputProperties.forEach(property -> selectProperties.add(makePropertyName(property, e1Properties, e2Properties)));
		
		return makePropertyString(selectProperties);
	}

	private SelectProperty makePropertyName(EventProperty property,
			List<EventProperty> e1Properties, List<EventProperty> e2Properties) {
		SelectProperty selectProperty = new SelectProperty();
		selectProperty.setAsName(property.getRuntimeName());
		String eventName;
	
		if (e1Properties.stream().anyMatch(e1Property -> e1Property.getRdfId().toString().equals(property.getRdfId().toString())))
			{
				eventName = "e1.";
				Optional<EventProperty> prop = e1Properties.stream().filter(e1Property -> e1Property.getRdfId().toString().equals(property.getRdfId().toString())).findFirst();
				if (prop.isPresent()) selectProperty.setOriginalName(eventName +prop.get().getRuntimeName());
			}
		else 
		{
			eventName = "e2.";
			Optional<EventProperty> prop = e2Properties.stream().filter(e2Property -> property.getRdfId().toString().startsWith(e2Property.getRdfId().toString())).findFirst();
			System.out.println(prop.isPresent());
			if (prop.isPresent()) selectProperty.setOriginalName(eventName +prop.get().getRuntimeName());
		}
		
		return selectProperty;
	}

	private String makePropertyString(List<SelectProperty> selectProperties) {
		String result = "";
		for(int i = 0; i < selectProperties.size(); i++)
		{
			result = result + selectProperties.get(i).getOriginalName() +" as " +selectProperties.get(i).getAsName();
			if (i < selectProperties.size()-1) result += ", ";
		}
		return result;
	}

	

}
