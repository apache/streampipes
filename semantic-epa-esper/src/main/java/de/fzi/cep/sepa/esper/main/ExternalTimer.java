package de.fzi.cep.sepa.esper.main;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.time.CurrentTimeEvent;

import de.fzi.cep.sepa.esper.debs.c1.StatusEvent;
import de.fzi.cep.sepa.esper.jms.IMessageListener;

public class ExternalTimer implements Runnable, IMessageListener {

	public static EPServiceProvider epService;
	
	private static final String brokerUrl = "tcp://localhost:61616";
	private static final String sourceTopic = "FZI.Timer";
	
	static {
		long initialTime = 1356994980000L;
		Configuration config = new Configuration();
//		config.getEngineDefaults().getThreading().setThreadPoolInbound(true);
//		config.getEngineDefaults().getThreading().setThreadPoolInboundNumThreads(5);
//		config.getEngineDefaults().getThreading().setThreadPoolOutbound(true);
//		config.getEngineDefaults().getThreading().setThreadPoolOutboundNumThreads(5);
		//config.setMetricsReportingEnabled();
		config.addEventType(StatusEvent.class);
		config.getEngineDefaults().getThreading().setInternalTimerEnabled(false);
		epService = EPServiceProviderManager.getDefaultProvider(config);
		epService.getEPRuntime().sendEvent(new CurrentTimeEvent(initialTime));
	}
	
	public ExternalTimer()
	{
		
	}
	
	@Override
	public void run() {
		//ActiveMQConsumer consumer = new ActiveMQConsumer(brokerUrl, sourceTopic);
		//consumer.setListener(this);
	}

	@Override
	public void onEvent(String json) {
		//CurrentTimeEvent timeEvent = new CurrentTimeEvent(Long.parseLong(json));
		//epService.getEPRuntime().sendEvent(timeEvent);
	}
	
}
