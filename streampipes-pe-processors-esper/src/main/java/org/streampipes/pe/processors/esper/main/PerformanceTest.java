package org.streampipes.pe.processors.esper.main;

import com.espertech.esper.client.EPServiceProvider;
import org.streampipes.wrapper.esper.EsperEngineSettings;

public class PerformanceTest {

	public static final String EVENT_NAME = "RandomNumberEvent";
	
	private static final String zookeeperUrl = "ipe-koi04.fzi.de";
	private static final int zookeeperPort = 2181;
	private static final String topic = "SEPA.SEP.Random.Number.Json";
	
	public static void main(String[] args) {
		
		int numberOfBlocks = Integer.parseInt(args[0]);
		int numberOfEvents = Integer.parseInt(args[1]);
		
		System.out.println(numberOfBlocks);
		System.out.println(numberOfEvents);
		
		EPServiceProvider provider = EsperEngineSettings.epService;
		
		provider.getEPAdministrator().getConfiguration().addEventType(EVENT_NAME, RandomNumberEvent.class);
		
		new PerformanceTestPatternGenerator(numberOfBlocks, numberOfEvents, provider.getEPAdministrator()).registerPatterns();
		
		Runnable r = new PerformanceTestFeeder(zookeeperUrl, zookeeperPort, topic, provider.getEPRuntime());
		
		Thread thread = new Thread(r);
		thread.start();
		
	}
}
