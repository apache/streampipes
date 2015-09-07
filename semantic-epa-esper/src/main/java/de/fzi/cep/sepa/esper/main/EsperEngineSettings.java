package de.fzi.cep.sepa.esper.main;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.client.metric.EngineMetric;
import com.espertech.esper.client.metric.StatementMetric;
import com.espertech.esper.client.soda.StreamSelector;
import com.espertech.esper.client.time.CurrentTimeEvent;

import de.fzi.cep.sepa.esper.debs.c1.StatusEvent;
import de.fzi.cep.sepa.esper.jms.IMessageListener;

public class EsperEngineSettings implements Runnable, IMessageListener {

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
//		config.getEngineDefaults().getThreading().setInternalTimerEnabled(false);
		config.getEngineDefaults().getStreamSelection()
	    .setDefaultStreamSelector(StreamSelector.RSTREAM_ISTREAM_BOTH);
		epService = EPServiceProviderManager.getDefaultProvider(config);
		epService.getEPRuntime().sendEvent(new CurrentTimeEvent(0));
		//enableMetrics();
	}
	
	public EsperEngineSettings()
	{
		
	}
	
	private static void enableMetrics() {
		
		EPStatement engineMetrics = epService.getEPAdministrator().createEPL("select * from com.espertech.esper.client.metric.EngineMetric");
		engineMetrics.addListener(new UpdateListener() {

			@Override
			public void update(EventBean[] arg0, EventBean[] arg1) {
				// TODO Auto-generated method stub
				for(EventBean bean : arg0)
				{
					System.out.println("***");
					EngineMetric engineMetric = (EngineMetric) bean.getUnderlying();
					System.out.println("Input Count: " +engineMetric.getInputCount());
					System.out.println("Input Count Delta: " +engineMetric.getInputCountDelta());
				}
			}
		});
		engineMetrics.start();
		
		EPStatement statementMetrics = epService.getEPAdministrator().createEPL("select * from com.espertech.esper.client.metric.StatementMetric");
		statementMetrics.addListener(new UpdateListener() {

			@Override
			public void update(EventBean[] arg0, EventBean[] arg1) {
				// TODO Auto-generated method stub
				for(EventBean bean : arg0)
				{
					StatementMetric statementMetric = (StatementMetric) bean.getUnderlying();
					System.out.println("***");
					System.out.println(statementMetric.getStatementName());
					System.out.println("CPU Time: " +statementMetric.getCpuTime());
				}
			}
		});
		//statementMetrics.start();
		
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
