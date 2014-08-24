package de.fzi.cep.sepa.runtime.routing;

import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;

import de.fzi.cep.sepa.runtime.EPEngine;

public class EPProducer extends DefaultProducer {

	private final EPEngine<?> engine;

	public EPProducer(Endpoint endpoint, EPEngine<?> engine) {
		super(endpoint);
		this.engine = engine;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void process(Exchange exchange) throws Exception { // incoming messages
		Object body = exchange.getIn().getBody(); 
		if (body instanceof Map) {
			String sourceInfo = String.valueOf(exchange.getIn().getHeaders().get("jmsdestination"));
			engine.onEvent((Map<String, Object>) body, sourceInfo);
		} else {
			throw new RuntimeException("Incoming event is not of type Map (" + body + ")");
		}
	}
}
