package org.streampipes.pe.processors.esper.main;

import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPStatement;

public class PerformanceTestPatternGenerator {

	private int numberOfBlocks;
	private long numberOfEvents;
	
	private EPAdministrator epAdmin;
	
	public PerformanceTestPatternGenerator(int numberOfBlocks, long numberOfEvents, EPAdministrator epAdmin) {
		this.numberOfBlocks = numberOfBlocks;
		this.numberOfEvents = numberOfEvents;
		
		this.epAdmin = epAdmin;
		
	}
	
	public void registerPatterns() {		
		
		for(int i = 1; i <= numberOfBlocks; i++) {
			String pattern = getPattern(i);
			System.out.println("Register pattern " +pattern);
			EPStatement statement = epAdmin.createEPL(pattern);
			
			if (i == numberOfBlocks) {
				System.out.println("Adding listener");
				statement.addListener(new PerformanceTestListener(numberOfBlocks, numberOfEvents));
			}
		}
		
	}
	
	
	private String getPattern(int i)
	{
		String insertInto = "insert into " +PerformanceTest.EVENT_NAME +i;
		
		String inputEventName = (i == 1) ? PerformanceTest.EVENT_NAME : PerformanceTest.EVENT_NAME +(i-1);
		
		String select = " select *, current_timestamp as appendedTime" +i +" from " +inputEventName;
		
		return insertInto + select;
	}
}
