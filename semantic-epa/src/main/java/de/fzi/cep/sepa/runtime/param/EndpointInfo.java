package de.fzi.cep.sepa.runtime.param;

import org.apache.camel.Endpoint;

import de.fzi.cep.sepa.runtime.routing.DestinationRoute;
import de.fzi.cep.sepa.runtime.routing.SourceRoute;

public class EndpointInfo { // any endpoint information

	private final String uri;

	private final DataType type;

	public EndpointInfo(String uri, DataType type) {
		this.uri = uri;
		this.type = type;
		
	}

	public static EndpointInfo of(String uri, DataType type) {
		return new EndpointInfo(uri, type);
	}

	public SourceRoute toSourceRoute(Endpoint agent) {
		return new SourceRoute(uri, type, agent);
	}

	public DestinationRoute toDestinationRoute(Endpoint agent) {
		return new DestinationRoute(uri, type, agent);
	}
}
