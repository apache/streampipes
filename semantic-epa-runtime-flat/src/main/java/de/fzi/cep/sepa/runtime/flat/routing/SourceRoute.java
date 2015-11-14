package de.fzi.cep.sepa.runtime.flat.routing;

import java.util.Map;

import de.fzi.cep.sepa.runtime.EPEngine;
import de.fzi.cep.sepa.runtime.flat.datatype.DatatypeDefinition;
import de.fzi.cep.sepa.runtime.flat.protocol.Consumer;
import de.fzi.cep.sepa.runtime.flat.protocol.IMessageListener;

public class SourceRoute extends Route implements IMessageListener {

	private EPEngine<?> epEngine;
	private Consumer consumer;
	
	public SourceRoute(String topic, String routeId, DatatypeDefinition dataType, Consumer consumer, EPEngine<?> engine) {
		super(routeId, dataType, topic);
		this.epEngine = engine;
		this.consumer = consumer;
	}

	@Override
	public void onEvent(String json) {
		Map<String, Object> result = dataType.unmarshal(json);
		epEngine.onEvent(result, topic);
	}

	@Override
	public void startRoute() {
		consumer.addListener(routeId, this);
	}

	@Override
	public void stopRoute() {
		consumer.removeListener(routeId);
	}

}
