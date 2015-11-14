package de.fzi.cep.sepa.runtime.camel.routing;

import org.apache.camel.Endpoint;
import org.apache.camel.builder.RouteBuilder;

import de.fzi.cep.sepa.runtime.camel.param.DataType;

public class SourceRoute extends RouteBuilder {

	private final String routeId;

	private final String sourceUri;

	private final DataType sourceType;

	private final Endpoint agentEndpoint;

	public SourceRoute(String sourceUri, DataType sourceType, Endpoint agentEndpoint) {
		this.sourceUri = sourceUri;
		this.sourceType = sourceType;
		this.agentEndpoint = agentEndpoint;
		routeId = sourceUri + " to " + agentEndpoint.getEndpointUri();
	}

	public final String getRouteId() {
		return routeId;
	}

	@Override
	public void configure() throws Exception {
		sourceType.unmarshal(from(sourceUri)).routeId(routeId).to(agentEndpoint);
	}
}
