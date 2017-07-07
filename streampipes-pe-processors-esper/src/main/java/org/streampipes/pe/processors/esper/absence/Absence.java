package org.streampipes.pe.processors.esper.absence;

import java.util.ArrayList;
import java.util.List;

import org.streampipes.pe.processors.esper.EsperEventEngine;

public class Absence extends EsperEventEngine<AbsenceParameters> {

	@Override
	protected List<String> statements(AbsenceParameters bindingParameters) {
		// select a,b from pattern[every a -> timer:interval(timeWindow) and not b]
		
		List<String> statements = new ArrayList<>();
		
		String statement = "select " +getSelectClause(bindingParameters) 
				+" from pattern[every a=" +fixEventName(bindingParameters.getInputStreamParams().get(0).getInName()) 
				+" -> timer:interval(" 
				+bindingParameters.getTimeWindowSize() 
				+" seconds) and not b=" 
				+fixEventName(bindingParameters.getInputStreamParams().get(1).getInName())
				+"]";
		
		statements.add(statement);
		
		return statements;
	}
	
	private String getSelectClause(AbsenceParameters params)
	{
		String result = "";
		for(String property : params.getSelectProperties())
		{
			result = result +"a." +property +" as " +property +", ";
		}
		result = result.substring(0, result.length()-2);
		return result;
	}

}
