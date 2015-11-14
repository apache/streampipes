package de.fzi.cep.sepa.runtime.camel.routing;

import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;

import de.fzi.cep.sepa.runtime.EPEngine;

public class CamelEPProducer extends DefaultProducer {

	private final EPEngine<?> engine;

	public CamelEPProducer(Endpoint endpoint, EPEngine<?> engine) {
		super(endpoint);
		this.engine = engine;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void process(Exchange exchange) throws Exception { // incoming messages
		Object body = exchange.getIn().getBody(); 
		if (body instanceof Map) {
			String sourceInfo;
			Object destinationHeader = exchange.getIn().getHeaders().get("jmsdestination");
			if (destinationHeader != null) sourceInfo = String.valueOf(destinationHeader);
			else sourceInfo = String.valueOf("topic://" +exchange.getIn().getHeaders().get("kafka.TOPIC"));
			engine.onEvent((Map<String, Object>) body, sourceInfo);
		} else {
			throw new RuntimeException("Incoming event is not of type Map (" + body + ")");
		}
	}
}
