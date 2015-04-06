package de.fzi.cep.sepa.esper;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.espertech.esper.client.EventBean;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.fzi.cep.sepa.esper.debs.c1.DebsOutputParameters;
import de.fzi.cep.sepa.esper.debs.c1.Challenge1FileWriter;
import de.fzi.cep.sepa.esper.debs.c1.OutputType;
import de.fzi.cep.sepa.esper.jms.ActiveMQPublisher;
import de.fzi.cep.sepa.runtime.OutputCollector;

public class OutputThread implements Runnable {

	public static Queue<Object> queue = new ConcurrentLinkedQueue<>();
	private OutputCollector collector;
	private ActiveMQPublisher publisher;
	private static ObjectMapper mapper = new ObjectMapper();;
	static int counter = 0;
	
	static String C1_FILENAME = "c:\\users\\riemer\\desktop\\debs-output-short-c1.txt";
	static String C2_FILENAME = "c:\\users\\riemer\\desktop\\debs-output-short-c2.txt";
	
	
	public OutputThread(OutputCollector collector, ActiveMQPublisher publisher)
	{
		this.collector = collector;
		this.publisher = publisher;
	}
	
	
	@Override
	public void run() {
		
		DebsOutputParameters params = new DebsOutputParameters(C1_FILENAME);	
		Challenge1FileWriter writer = new Challenge1FileWriter(params, OutputType.HIDE);
		
		
		long currentTimestamp = System.currentTimeMillis();
		for(;;)
		{
			if (System.currentTimeMillis()-currentTimestamp > 20000) break;
			if (!queue.isEmpty())
			{
				Object newEvents = queue.poll();
				if (newEvents instanceof EventBean[]) 
					{
						currentTimestamp = System.currentTimeMillis();
						counter++;
						if (counter % 100000 == 0) System.out.println(counter + " Events processed.");
						EventBean[] beans = (EventBean[]) newEvents;
						writer.onEvent(beans[0]);
					}
			}	
		}
		System.out.println("20 seconds without new events arriving: Output thread stopped");
		
	}
	
	/*
	public void run()
	{
		counter++;
		if (counter % 10000 == 0) System.out.println(counter + " Events processed.");

		StringBuilder builder = new StringBuilder();
		for(EventBean bean : beans)
		{
			bean.eq
			//builder.append(renderer.render(bean));
			try {
				builder.append(mapper.writeValueAsString(bean.getUnderlying()));
				
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		publisher.send(builder.toString());
	}
	
	*/
	public static void add(Object object)
	{
		queue.add(object);
	}
}
