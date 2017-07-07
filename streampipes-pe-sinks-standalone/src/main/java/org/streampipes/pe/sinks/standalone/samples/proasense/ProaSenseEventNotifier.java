package org.streampipes.pe.sinks.standalone.samples.proasense;

public class ProaSenseEventNotifier {

	private int counter;
	private String eventName;
	
	public ProaSenseEventNotifier(String eventName)
	{
		this.eventName = eventName;
		this.counter = 0;
	}
	
	public void increaseCounter()
	{
		counter += 1;
	}
	
	public int getCounter()
	{
		return counter;
	}
	
	public String getEventName()
	{
		return eventName;
	}
}
