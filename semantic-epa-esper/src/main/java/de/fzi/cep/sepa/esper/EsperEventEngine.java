package de.fzi.cep.sepa.esper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jms.JMSException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.espertech.esper.client.ConfigurationException;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.client.metric.EngineMetric;
import com.espertech.esper.client.metric.StatementMetric;
import com.espertech.esper.client.util.JSONEventRenderer;
import com.google.gson.Gson;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.esper.config.EsperConfig;
import de.fzi.cep.sepa.esper.jms.ActiveMQPublisher;
import de.fzi.cep.sepa.esper.main.ExternalTimer;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.graph.SEPAInvocationGraph;
import de.fzi.cep.sepa.runtime.EPEngine;
import de.fzi.cep.sepa.runtime.OutputCollector;
import de.fzi.cep.sepa.runtime.param.BindingParameters;
import de.fzi.cep.sepa.runtime.param.EngineParameters;

public abstract class EsperEventEngine<T extends BindingParameters> implements EPEngine<T>{

	protected EPServiceProvider epService;
	protected List<EPStatement> epStatements;
	private ActiveMQPublisher publisher;
	
	private static final Logger logger = LoggerFactory.getLogger(EsperEventEngine.class.getSimpleName());
	
	@Override
	public void bind(EngineParameters<T> parameters, OutputCollector collector, SEPAInvocationGraph graph) {
		if (parameters.getInEventTypes().size() != 1)
			throw new IllegalArgumentException("Event Rate only possible on one event type.");
			
		epService = ExternalTimer.epService;
		//epService = EPServiceProviderManager.getDefaultProvider(conf);
		//enableMetrics();
		EventGrounding grounding = graph.getOutputStream().getEventGrounding();
		
		try {
			publisher = new ActiveMQPublisher(grounding.getUri() +":" +grounding.getPort(), grounding.getTopicName());
			
		} catch (JMSException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	
		logger.info("Configuring event types for graph " +graph.getName());
		parameters.getInEventTypes().entrySet().forEach(e -> {
			Map inTypeMap = e.getValue();
			//registerEventTypeIfNotExists(e.getKey(), inTypeMap); // indirect cast from Class to Object
			checkAndRegisterEventType(e.getKey(), inTypeMap);
		});
		//MapUtils.debugPrint(System.out, "topic://" +graph.getOutputStream().getEventGrounding().getTopicName(), parameters.getOutEventType());
		checkAndRegisterEventType("topic://" +graph.getOutputStream().getEventGrounding().getTopicName(), parameters.getOutEventType());
		
		List<String> statements = statements(parameters.getStaticProperty());
		registerStatements(statements, collector, parameters.getStaticProperty());
		
		
	}
	
	private void enableMetrics() {
		
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

	private void checkAndRegisterEventType(String key, Map<String, Object> typeMap)
	{
		Map<String, Object> newTypeMap = new HashMap<String, Object>();
		Iterator<String> it = typeMap.keySet().iterator();
		while(it.hasNext())
		{
			String objKey = it.next();
			Object obj = typeMap.get(objKey);
			if (obj instanceof java.util.List)
			{
				String eventName = StringUtils.capitalize(objKey);
				registerEventTypeIfNotExists(eventName, (Map<String, Object>) ((java.util.List) obj).get(0));
				newTypeMap.put(objKey, eventName +"[]");
			}
			else {
				newTypeMap.put(objKey, obj);
			}			
		}
		//MapUtils.debugPrint(System.out, key, newTypeMap);
		registerEventTypeIfNotExists(key, newTypeMap);
		
	}
	
	private void registerEventTypeIfNotExists(String eventTypeName, Map<String, Object> typeMap)
	{ 	
		try {
			logger.info("Registering event type, " +eventTypeName);
			epService.getEPAdministrator().getConfiguration().addEventType(eventTypeName, typeMap);
		} catch (ConfigurationException e)
		{
			e.printStackTrace();
			logger.info("Event type does already exist, " +eventTypeName);
		}
	}
	
	private void registerStatements(List<String> statements, OutputCollector collector, T params)
	{
		toEpStatement(statements);
		//OutputThread outputThread = new OutputThread(collector, publisher);
		//new Thread(outputThread).start();
		StatementAwareQueue outputThread = new StatementAwareQueue(getWriter(collector, params), 500000, 20);
		outputThread.start();
		for(EPStatement epStatement : epStatements)
		{
			logger.info("Registering statement " +epStatement.getText());
			
			if (epStatement.getText().startsWith("select")) 
			{
				epStatement.addListener(listenerSendingTo(outputThread));
				//epStatement.addListener(listenerSendingTo(collector));
			}
			epStatement.start();
			
		}
	}
	
	private void toEpStatement(List<String> statements)
	{
		if (epStatements == null) epStatements = new ArrayList<>();
		for(String statement : statements)
		{
			epStatements.add(epService.getEPAdministrator().createEPL(statement));
		}
	}

	@Override
	public void onEvent(Map<String, Object> event, String sourceInfo) {
		//logger.info("New event: {}", event);
		epService.getEPRuntime().sendEvent(event, sourceInfo);
	}

	@Override
	public void discard() {
		// TODO Auto-generated method stub
		
	}
	
	private static UpdateListener listenerSendingTo(AbstractQueueRunnable<EventBean[]> abstractQueue) {
		return new UpdateListener() {
				
			@Override
			public void update(EventBean[] newEvents, EventBean[] oldEvents) {
				//OutputThread.queue.offer(newEvents);	
				try {
					abstractQueue.add(newEvents);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				/*
				if (newEvents != null && newEvents.length == 1) {
					//logger.info("SINGLE , Sending event {} ", newEvents[0].getUnderlying());
					//collector.send(newEvents[0].getUnderlying());
					outputThread.add(newEvents[0].getUnderlying());
				} else if (newEvents != null && newEvents.length > 1){
					Object[] events = new Object[newEvents.length];
					for(int i = 0; i < newEvents.length; i++)
					{
						events[i] = newEvents[i].getUnderlying();
					}
					outputThread.add(events);
					//logger.info("ARRAY, Sending event {} ", events);
					//collector.send(events);
				} else {
					//logger.info("Triggered listener but there is no new event");
				}		
				*/
			}
		};
	}
	
	protected abstract List<String> statements(final T bindingParameters);

	protected String fixEventName(String eventName)
	{
		return "`" +eventName +"`";
	}
	
	protected List<String> makeStatementList(String statement)
	{
		return Utils.createList(statement);
	}
	/*
	protected boolean discard()
	{
		epService.destroy();
	}
	*/
	
	protected Writer getWriter(OutputCollector collector, T params)
	{
		return EsperConfig.getDefaultWriter(collector, params); 
	}
}
