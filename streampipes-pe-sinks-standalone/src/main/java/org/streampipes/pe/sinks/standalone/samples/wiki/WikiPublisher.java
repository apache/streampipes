package org.streampipes.pe.sinks.standalone.samples.wiki;

import org.streampipes.messaging.EventListener;
import org.streampipes.messaging.EventProducer;

public class WikiPublisher implements EventListener<byte[]> {

	private EventProducer producer;

	public WikiPublisher(EventProducer producer)
	{
		this.producer = producer;
	}
	
	@Override
	public void onEvent(byte[] message) {
		producer.publish(message);
	}

}
