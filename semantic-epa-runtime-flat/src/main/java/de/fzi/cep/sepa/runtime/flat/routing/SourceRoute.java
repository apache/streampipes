package de.fzi.cep.sepa.runtime.flat.routing;

import java.util.Map;

import de.fzi.cep.sepa.runtime.EPEngine;
import de.fzi.cep.sepa.runtime.flat.manager.ProtocolManager;
import de.fzi.cep.sepa.runtime.flat.protocol.Consumer;
import de.fzi.cep.sepa.runtime.flat.protocol.ConsumerMessageListener;

public class SourceRoute extends Route implements ConsumerMessageListener {

	private EPEngine<?> epEngine;
	private Consumer<?> consumer;
	
	public SourceRoute(String topic, String routeId, Consumer<?> consumer, EPEngine<?> engine) {
		super(routeId, topic);
		this.epEngine = engine;
		this.consumer = consumer;
	}

	@Override
	public void onEvent(Map<String, Object> event) {
		if (ProtocolManager.isTopicLeader(topic, routeId)) epEngine.onEvent(event, topic);
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
