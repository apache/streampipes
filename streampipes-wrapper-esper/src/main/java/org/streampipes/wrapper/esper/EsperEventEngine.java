package org.streampipes.wrapper.esper;

import com.espertech.esper.client.ConfigurationException;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.event.map.MapEventBean;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.commons.Utils;
import org.streampipes.wrapper.esper.config.EsperConfig;
import org.streampipes.wrapper.esper.writer.Writer;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;
import org.streampipes.wrapper.routing.EventProcessorOutputCollector;
import org.streampipes.wrapper.runtime.EventProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class EsperEventEngine<T extends EventProcessorBindingParams> implements EventProcessor<T> {

	protected EPServiceProvider epService;
	protected List<EPStatement> epStatements;	
	
	private AbstractQueueRunnable<MapEventBean[]> queue;
	private List<String> eventTypeNames = new ArrayList<>();
	
	private static int i = 0;
	private long lastTimestamp = 0;
	
	private static final Logger logger = LoggerFactory.getLogger(EsperEventEngine.class.getSimpleName());
	
	@Override
	public void bind(T parameters, EventProcessorOutputCollector collector) {
		if (parameters.getInEventTypes().size() != parameters.getGraph().getInputStreams().size())
			throw new IllegalArgumentException("Input parameters do not match!");
			
		epService = EsperEngineSettings.epService;

		System.out.println("Configuring event types for graph " +parameters.getGraph().getName());
		parameters.getInEventTypes().entrySet().forEach(e -> {
			Map inTypeMap = e.getValue();
			checkAndRegisterEventType(e.getKey(), inTypeMap);
		});
		
		//MapUtils.debugPrint(System.out, "topic://" +graph.getOutputStream().getEventGrounding().getTopicName(), parameters.getOutEventType());
		checkAndRegisterEventType("topic://" +parameters.getGraph().getOutputStream().getEventGrounding()
						.getTransportProtocol().getTopicName(), parameters.getOutEventType());
		
		List<String> statements = statements(parameters);
		registerStatements(statements, collector, parameters);
		
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
			System.out.println("Registering event type, " +eventTypeName);
			epService.getEPAdministrator().getConfiguration().addEventType(eventTypeName, typeMap);
			eventTypeNames.add(eventTypeName);
		} catch (ConfigurationException e)
		{
			e.printStackTrace();
			System.out.println("Event type does already exist, " +eventTypeName);
		}
	}
	
	private void registerStatements(List<String> statements, EventProcessorOutputCollector collector, T params)
	{
		toEpStatement(statements);
		queue = new StatementAwareQueue(getWriter(collector, params), 500000);
		queue.start();
		for(EPStatement epStatement : epStatements)
		{
			logger.info("Registering statement " +epStatement.getText());
			
			if (epStatement.getText().startsWith("select")) 
			{
				epStatement.addListener(listenerSendingTo(queue));
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
		//MapUtils.debugPrint(System.out, "", event);
		//if (i % 10000 == 0) System.out.println(i +" in Esper.");
		i++;
		epService.getEPRuntime().sendEvent(event, sourceInfo);
	}

	@Override
	public void discard() {
		logger.info("Removing existing statements");
		for(EPStatement epStatement : epStatements)
		{
			epService.getEPAdministrator().getStatement(epStatement.getName()).removeAllListeners();
			epService.getEPAdministrator().getStatement(epStatement.getName()).stop();
			epService.getEPAdministrator().getStatement(epStatement.getName()).destroy();		
		}
		epStatements.clear();
		for(String eventName : eventTypeNames) 
			{
				try {
					epService.getEPAdministrator().getConfiguration().removeEventType(eventName, false);
				} catch (ConfigurationException ce)
				{
					logger.info("Event type used in another statement which is still running, skipping...");
				}
			}
		
		queue.interrupt();
	}
	
	private static UpdateListener listenerSendingTo(AbstractQueueRunnable<MapEventBean[]> queue) {
		return new UpdateListener() {
				
			@Override
			public void update(EventBean[] newEvents, EventBean[] oldEvents) {
				try {
					if (newEvents != null) queue.add((MapEventBean[]) newEvents);
					else queue.add((MapEventBean[]) oldEvents);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
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
		
	protected Writer getWriter(EventProcessorOutputCollector collector, T params)
	{
		return EsperConfig.getDefaultWriter(collector, params);
	}
}
