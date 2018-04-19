package org.streampipes.wrapper.esper;

import com.espertech.esper.client.ConfigurationException;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.commons.Utils;
import org.streampipes.wrapper.esper.config.EsperEngineConfig;
import org.streampipes.wrapper.esper.writer.Writer;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;
import org.streampipes.wrapper.routing.SpOutputCollector;
import org.streampipes.wrapper.runtime.EventProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class EsperEventEngine<T extends EventProcessorBindingParams> extends EventProcessor<T> {

	protected EPServiceProvider epService;
	protected List<EPStatement> epStatements;	
	
	private AbstractQueueRunnable<EventBean[]> queue;
	private List<String> eventTypeNames = new ArrayList<>();
	
	private static final Logger LOG = LoggerFactory.getLogger(EsperEventEngine.class);

	public EsperEventEngine(T bindingParams) {
		super(bindingParams);
	}

	@Override
	public void bind(T parameters, SpOutputCollector collector) {
		if (parameters.getInEventTypes().size() != parameters.getGraph().getInputStreams().size())
			throw new IllegalArgumentException("Input parameters do not match!");
			
		epService = EsperEngineSettings.epService;

		LOG.info("Configuring event types for graph " +parameters.getGraph().getName());
		parameters.getInEventTypes().entrySet().forEach(e -> {
			Map inTypeMap = e.getValue();
			checkAndRegisterEventType(e.getKey(), inTypeMap);
		});
		
		//MapUtils.debugPrint(System.out, "topic://" +graph.getOutputStream().getEventGrounding().getTopicName(), parameters.getOutEventType());
		checkAndRegisterEventType(parameters.getGraph().getOutputStream().getEventGrounding()
						.getTransportProtocol().getTopicDefinition().getActualTopicName(), parameters.getOutEventType());
		
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
			LOG.info("Registering event type, " +eventTypeName);
			epService.getEPAdministrator().getConfiguration().addEventType(eventTypeName, typeMap);
			eventTypeNames.add(eventTypeName);
		} catch (ConfigurationException e)
		{
			e.printStackTrace();
			LOG.error("Event type does already exist, " +eventTypeName);
		}
	}
	
	private void registerStatements(List<String> statements, SpOutputCollector collector, T params)
	{
		toEpStatement(statements);
		queue = new StatementAwareQueue(getWriter(collector, params), 500000);
		queue.start();
		for(EPStatement epStatement : epStatements)
		{
			LOG.info("Registering statement " +epStatement.getText());
			
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
		epService.getEPRuntime().sendEvent(event, sourceInfo);
	}

	@Override
	public void discard() {
		LOG.info("Removing existing statements");
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
					LOG.error("Event type used in another statement which is still running, skipping...");
				}
			}
		
		queue.interrupt();
	}
	
	private static UpdateListener listenerSendingTo(AbstractQueueRunnable<EventBean[]> queue) {
		return new UpdateListener() {
				
			@Override
			public void update(EventBean[] newEvents, EventBean[] oldEvents) {
				try {
					if (newEvents != null) queue.add((EventBean[]) newEvents);
					else queue.add((EventBean[]) oldEvents);
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
		
	protected Writer getWriter(SpOutputCollector collector, T params)
	{
		return EsperEngineConfig.getDefaultWriter(collector, params);
	}
}
