package org.streampipes.pe.processors.esper.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

import org.streampipes.commons.config.old.ConfigurationManager;

public class PerformanceTestListener implements UpdateListener {

	private int counter = 0;
	private PrintWriter stream;
	
	private StringBuilder outputCollector;
	
	private int numberOfBlocks;
	private long numberOfEvents;
		
	public PerformanceTestListener(int numberOfBlocks, long numberOfEvents) {
		this.numberOfBlocks = numberOfBlocks;
		this.numberOfEvents = numberOfEvents;
		this.outputCollector = new StringBuilder();
		prepare();
	}
	
	@Override
	public void update(EventBean[] newEvents, EventBean[] oldEvents) {
		if (counter % numberOfEvents == 0) 
		{
			System.out.println(counter + " Event processed."); 
			stream.write(outputCollector.toString());	
			outputCollector.setLength(0);
		}
		counter++;
		
		EventBean outputEvent = newEvents[0];
	
		long currentTimestamp = System.currentTimeMillis();
		StringBuilder output = new StringBuilder();
		output.append(counter);
		output.append(",");
		
		
		for(String propertyName : outputEvent.getEventType().getPropertyNames()) {
		output.append(outputEvent.get(propertyName));
		output.append(",");
		}
		
		output.append(currentTimestamp);
		output.append(",");
		output.append(currentTimestamp - Long.parseLong(String.valueOf(outputEvent.get("timestamp"))));
		output.append(System.lineSeparator());
		outputCollector.append(output);
	
		
	}	
	
	private void prepare()
	{
		File file = new File(ConfigurationManager.getStreamPipesConfigFileLocation() +getFilename());
		try {
			stream = new PrintWriter(new FileOutputStream(file, true), true);
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	private String getFilename() {
		Calendar calendar = Calendar.getInstance();
		return "evaluation-esper-" 
				+numberOfBlocks 
				+"-" 
				+numberOfEvents
				+"-"
				+calendar.get(Calendar.YEAR)
				+"-"
				+calendar.get(Calendar.MONTH)+1
				+"-"
				+calendar.get(Calendar.DAY_OF_MONTH)
				+"-"
				+calendar.get(Calendar.HOUR_OF_DAY)
				+"-"
				+calendar.get(Calendar.MINUTE)
				+".csv";
	}
}
