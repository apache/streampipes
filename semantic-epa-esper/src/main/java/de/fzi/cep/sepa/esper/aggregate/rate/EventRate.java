package de.fzi.cep.sepa.esper.aggregate.rate;

import java.util.HashMap;
import java.util.Map;



import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

import de.fzi.cep.sepa.runtime.EPEngine;
import de.fzi.cep.sepa.runtime.OutputCollector;
import de.fzi.cep.sepa.runtime.param.EngineParameters;

public class EventRate implements EPEngine<EventRateParameter>{
	
	private EPServiceProvider epService;

	private static final Logger logger = LoggerFactory.getLogger(EventRate.class.getSimpleName());

	private String EVENT_NAME_PARAM = "name";

	@Override
	public void bind(EngineParameters<EventRateParameter> parameters,
			OutputCollector collector) {
		if (parameters.getInEventTypes().size() != 1)
			throw new IllegalArgumentException("Event Rate only possible on one event type.");
		
		parameters.getInEventTypes().keySet().forEach(e -> {
			EVENT_NAME_PARAM = e;
		});
		
		Configuration config = new Configuration();
		parameters.getInEventTypes().entrySet().forEach(e -> {
			Map inTypeMap = e.getValue();
			config.addEventType(e.getKey(), inTypeMap); // indirect cast from Class to Object
		});
		
		
		System.out.println(parameters.getStaticProperty().getOutputName());
		Map<String, Object> outMap = new HashMap<>();
		outMap.put("rate", java.lang.Double.class);
		config.addEventType(parameters.getStaticProperty().getOutputName(), outMap);
		
		
		epService = EPServiceProviderManager.getProvider(RandomStringUtils.randomAlphabetic(8), config);
		System.out.println(statement(parameters.getStaticProperty()));
		EPStatement statement = epService.getEPAdministrator().createEPL(statement(parameters.getStaticProperty()));
		
	
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

	private String statement(final EventRateParameter params) {
		String outName = "`" +params.getOutName() +"`";
		String inName = "`" +params.getInName() +"`";
		String epl = "insert into " +outName +" select rate(" +params.getAvgRate() +") as rate from " +inName +" output snapshot every " +params.getOutputRate() +" sec";
		
		return epl;
		
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
