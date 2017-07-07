package org.streampipes.pe.processors.esper.hella.minshuttletime;

import org.streampipes.pe.processors.esper.EsperEventEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MinShuttleTime extends EsperEventEngine<MinShuttleTimeParameters> {

	@Override
	protected List<String> statements(MinShuttleTimeParameters bindingParameters) {
		
		List<String> statements = new ArrayList<>();
		
		Map<String, Map<String, Integer>> minTime = new HashMap<String, Map<String, Integer>>();
		
		Map<String, Integer> pm1 = new HashMap<String, Integer>();
		pm1.put("IMM2", 77000);
		pm1.put("IMM1", 55000);

		//pm1.put("IMM2", 9625);
		//pm1.put("IMM1", 6875);
		
		Map<String, Integer> pm2 = new HashMap<String, Integer>();
		pm2.put("IMM4",  117000);
		pm2.put("IMM3", 91000);
		pm2.put("IMM5", 137000);

		//pm2.put("IMM4",  7300);
		//pm2.put("IMM3", 5750);
		//pm2.put("IMM5", 8500);
		
		minTime.put("PM1", pm1);
		minTime.put("PM2", pm2);
		
		for(String lacqueringLineKey : minTime.keySet())
		{
			for(String mouldingMachineKey : minTime.get(lacqueringLineKey).keySet())
			{
				String statement = "select " +bindingParameters.getLacqueringLineIdEventName() +" as lacqueringLineId, " 
						+bindingParameters.getMouldingMachineIdEventName() +" as mouldingMachineId, "
						+bindingParameters.getTimestampEventName() +" as timeDifference "
						+"from " +fixEventName(bindingParameters.getInputStreamParams().get(0).getInName())
						+" where " +bindingParameters.getTimestampEventName() +" < (1.2*" +minTime.get
								(lacqueringLineKey).get(mouldingMachineKey) +")"
						+" and " +bindingParameters.getLacqueringLineIdEventName() +" = '" +lacqueringLineKey +"' and "
						+bindingParameters.getMouldingMachineIdEventName() +" = '" +mouldingMachineKey +"'";
				
				statements.add(statement);
				System.out.println(statement);
			}
		}
		return statements;
	}
	
	private long getMinShuttleTime(String lacqueringLineId, String mouldingMachineId)
	{
		
		return 0;
	}
	
}
