package de.fzi.cep.sepa.esper.filter.text;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.client.soda.EPStatementObjectModel;
import com.espertech.esper.client.soda.Expression;
import com.espertech.esper.client.soda.Expressions;
import com.espertech.esper.client.soda.FilterStream;
import com.espertech.esper.client.soda.FromClause;
import com.espertech.esper.client.soda.InsertIntoClause;
import com.espertech.esper.client.soda.SelectClause;

import de.fzi.cep.sepa.esper.util.StringOperator;
import de.fzi.cep.sepa.runtime.EPEngine;
import de.fzi.cep.sepa.runtime.OutputCollector;
import de.fzi.cep.sepa.runtime.param.EngineParameters;

public class TextFilter implements EPEngine<TextFilterParameter>{
	
	private EPServiceProvider epService;

	private static final Logger logger = LoggerFactory.getLogger(TextFilter.class.getSimpleName());

	private String EVENT_NAME_PARAM = "name";

	@Override
	public void bind(EngineParameters<TextFilterParameter> parameters,
			OutputCollector collector) {
		if (parameters.getInEventTypes().size() != 1)
			throw new IllegalArgumentException("Text Filter only possible on one event type.");
		
		parameters.getInEventTypes().keySet().forEach(e -> {
			EVENT_NAME_PARAM = e;
		});
		
		Configuration config = new Configuration();
		parameters.getInEventTypes().entrySet().forEach(e -> {
			Map inTypeMap = e.getValue();
			config.addEventType(e.getKey(), inTypeMap); // indirect cast from Class to Object
		});
		
		epService = EPServiceProviderManager.getDefaultProvider(config);

		EPStatementObjectModel model = statement(parameters.getStaticProperty());
		EPStatement statement = epService.getEPAdministrator().create(model);
		
	
		statement.addListener(listenerSendingTo(collector));
		statement.start();
		

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

	private EPStatementObjectModel statement(final TextFilterParameter params) {
		EPStatementObjectModel model = new EPStatementObjectModel();
		model.insertInto(new InsertIntoClause(params.getOutName())); // out name
		model.selectClause(SelectClause.createWildcard());
		model.fromClause(new FromClause().add(FilterStream.create(params.getInName()))); // in name
		
		Expression stringFilter;
		if (params.getStringOperator() == StringOperator.MATCHES)
			stringFilter = Expressions.eq(params.getFilterProperty(), params.getKeyword());
		else
			stringFilter = Expressions.like(params.getFilterProperty(), "%" +params.getKeyword() +"%");
	
		model.whereClause(stringFilter);
		logger.info("Generated EPL: " +model.toEPL());
		return model;
		
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

}
