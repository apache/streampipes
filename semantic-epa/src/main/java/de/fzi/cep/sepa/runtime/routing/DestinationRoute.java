package de.fzi.cep.sepa.runtime.routing;

import org.apache.camel.Endpoint;
import org.apache.camel.builder.RouteBuilder;

import de.fzi.cep.sepa.runtime.param.DataType;

public class DestinationRoute extends RouteBuilder {

	private final String routeId;

	private final String destUri;

	private final DataType destType;

	private final Endpoint agentEndpoint;

	public DestinationRoute(String destUri, DataType destType, Endpoint agentEndpoint) {
		this.destUri = destUri;
		this.destType = destType;
		this.agentEndpoint = agentEndpoint;
		routeId = destUri + " to " + agentEndpoint.getEndpointUri();
	}

	public final String getRouteId() {
		return routeId;
	}

	@Override
	public void configure() throws Exception {
		destType.marshal(from(agentEndpoint)).routeId(routeId).to(destUri);
	}
}
