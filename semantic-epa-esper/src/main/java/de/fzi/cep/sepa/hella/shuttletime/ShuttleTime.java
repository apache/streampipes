package de.fzi.cep.sepa.hella.shuttletime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.fzi.cep.sepa.esper.EsperEventEngine;


public class ShuttleTime extends EsperEventEngine<ShuttleTimeParameters> {

	@Override
	protected List<String> statements(ShuttleTimeParameters bindingParameters) {
		
		List<String> statements = new ArrayList<>();
		List<String> lacqueringLineIds = Arrays.asList("PM1", "PM2");
		
		for(String lacqueringLineId : lacqueringLineIds)
		{
			for(MouldingMachine mm : bindingParameters.getMouldingMachines())
			{
				String statement = "select a." +bindingParameters.getShuttleIdEventName() +" as shuttleId, b."
						+bindingParameters.getLocationEventName() +" as mouldingMachineId, a."
						+bindingParameters.getLocationEventName() +" as lacqueringLineId, (b."
						+bindingParameters.getTimestampEventName() +" - a." +bindingParameters.getTimestampEventName() +") as timeDifference " 
						+" from pattern[every a=" +fixEventName(bindingParameters.getInputStreamParams().get(0).getInName()) 
						+"(location='" +lacqueringLineId +"', event='WorkDone (Automatic)')"
						+" -> b=" +fixEventName(bindingParameters.getInputStreamParams().get(0).getInName()) 
						+"(location='" +mm.getMachineId() +"', event='Arrive')"
						+"] where a." +bindingParameters.getShuttleIdEventName() +"=b." +bindingParameters.getShuttleIdEventName();
				
				statements.add(statement);
				System.out.println(statement);
			}
		}
		return statements;
	}
	
}
