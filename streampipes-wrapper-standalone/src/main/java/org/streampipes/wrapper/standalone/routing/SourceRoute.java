package org.streampipes.wrapper.standalone.routing;

import java.util.Map;

import org.streampipes.wrapper.EPEngine;
import org.streampipes.wrapper.standalone.manager.ProtocolManager;
import org.streampipes.wrapper.standalone.protocol.Consumer;
import org.streampipes.wrapper.standalone.protocol.ConsumerMessageListener;

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
