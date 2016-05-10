package de.fzi.cep.sepa.runtime.camel;

import java.util.List;

import static java.util.stream.Collectors.toList;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;

import de.fzi.cep.sepa.runtime.EPRuntime;
import de.fzi.cep.sepa.runtime.camel.param.CamelConfig;
import de.fzi.cep.sepa.runtime.camel.param.CamelRuntimeParameters;
import de.fzi.cep.sepa.runtime.camel.routing.CamelEPEndpoint;
import de.fzi.cep.sepa.runtime.camel.routing.DestinationRoute;
import de.fzi.cep.sepa.runtime.camel.routing.SourceRoute;

public class CamelEPRuntime extends EPRuntime { // routing container


	private final CamelContext context;
	private final List<CamelConfig> configs; // for removal

	private final Endpoint endpoint;
	private final List<SourceRoute> sources;
	private final DestinationRoute destination;


	public CamelEPRuntime(CamelContext context, CamelRuntimeParameters<?> params) {
		super(params);
		this.context = context;
		configs = params.getConfigs();
		configs.forEach(config -> config.applyTo(context));
		
		endpoint = new CamelEPEndpoint(this, params.getUri(), context);
		sources = params.getSources().stream().map(source -> source.toSourceRoute(endpoint)).collect(toList());
		destination = params.getDestination().toDestinationRoute(endpoint);

	}

	@Override
	public void preDiscard() {
		try {
			for (SourceRoute source : sources)
			{
				context.stopRoute(source.getRouteId());
				context.removeRoute(source.getRouteId());
			}
			context.stopRoute(destination.getRouteId());
			context.removeRoute(destination.getRouteId());
		} catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public void postDiscard() {
		try {
			for (CamelConfig config : configs)
				config.removeFrom(context);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void initRuntime() {
		try {
			for (SourceRoute source : sources)
				context.addRoutes(source);
			context.addRoutes(destination);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
