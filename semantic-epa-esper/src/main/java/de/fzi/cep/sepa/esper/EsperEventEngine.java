package de.fzi.cep.sepa.esper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.espertech.esper.client.ConfigurationException;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import com.google.gson.Gson;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.model.impl.graph.SEPAInvocationGraph;
import de.fzi.cep.sepa.runtime.EPEngine;
import de.fzi.cep.sepa.runtime.OutputCollector;
import de.fzi.cep.sepa.runtime.param.BindingParameters;
import de.fzi.cep.sepa.runtime.param.EngineParameters;

public abstract class EsperEventEngine<T extends BindingParameters> implements EPEngine<T>{

	protected EPServiceProvider epService;
	protected List<EPStatement> epStatements;
	protected Gson gson = new Gson();
	
	private static final Logger logger = LoggerFactory.getLogger(EsperEventEngine.class.getSimpleName());
	
	@Override
	public void bind(EngineParameters<T> parameters, OutputCollector collector, SEPAInvocationGraph graph) {
		if (parameters.getInEventTypes().size() != 1)
			throw new IllegalArgumentException("Event Rate only possible on one event type.");
			
		epService = EPServiceProviderManager.getDefaultProvider();
		
		logger.info("Configuring event types for graph " +graph.getName());
		parameters.getInEventTypes().entrySet().forEach(e -> {
			Map inTypeMap = e.getValue();
			registerEventTypeIfNotExists(e.getKey(), inTypeMap); // indirect cast from Class to Object
		});
		
		registerEventTypeIfNotExists(graph.getOutputStream().getEventGrounding().getTopicName(), graph.getOutputStream().getEventSchema().toUntypedRuntimeMap());
		
		List<String> statements = statements(parameters.getStaticProperty());
		registerStatements(statements, collector);
		
	}
	
	private void registerEventTypeIfNotExists(String eventTypeName, Map<String, Object> typeMap)
	{ 	
		try {
			logger.info("Registering event type, " +eventTypeName);
			epService.getEPAdministrator().getConfiguration().addEventType(eventTypeName, typeMap);
		} catch (ConfigurationException e)
		{
			logger.info("Event type does already exist, " +eventTypeName);
		}
	}
	
	private void registerStatements(List<String> statements, OutputCollector collector)
	{
		toEpStatement(statements);
		for(EPStatement epStatement : epStatements)
		{
			logger.info("Registering statement " +epStatement.getText());
			epStatement.addListener(listenerSendingTo(collector));
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
		logger.info("New event: {}", event);
		epService.getEPRuntime().sendEvent(event, sourceInfo);
	}

	@Override
	public void discard() {
		// TODO Auto-generated method stub
		
	}
	
	private static UpdateListener listenerSendingTo(OutputCollector collector) {
		return new UpdateListener() {
			@Override
			public void update(EventBean[] newEvents, EventBean[] oldEvents) {
				if (newEvents != null && newEvents.length > 0) {
					logger.info("Sending event {} ", newEvents[0].getUnderlying());
					collector.send(newEvents[0].getUnderlying());
				} else {
					logger.info("Triggered listener but there is no new event");
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
}
