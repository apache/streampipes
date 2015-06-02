package de.fzi.cep.sepa.runtime;

import java.util.List;

import static java.util.stream.Collectors.toList;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;

import de.fzi.cep.sepa.runtime.param.CamelConfig;
import de.fzi.cep.sepa.runtime.param.RuntimeParameters;
import de.fzi.cep.sepa.runtime.routing.DestinationRoute;
import de.fzi.cep.sepa.runtime.routing.EPEndpoint;
import de.fzi.cep.sepa.runtime.routing.SourceRoute;

public class EPRuntime { // routing container

	private final EPEngine<?> engine;

	private final CamelContext context;
	private final List<CamelConfig> configs; // for removal

	private final Endpoint endpoint;
	private final List<SourceRoute> sources;
	private final DestinationRoute destination;

	private final OutputCollector collector;

	public EPRuntime(CamelContext context, RuntimeParameters<?> params) {
		this.context = context;
		configs = params.getConfigs();
		configs.forEach(config -> config.applyTo(context));
		this.collector = new OutputCollector();

		engine = params.getPreparedEngine(this, params.getEngineParameters().getGraph(), collector);

		endpoint = new EPEndpoint(this, params.getUri(), context);
		sources = params.getSources().stream().map(source -> source.toSourceRoute(endpoint)).collect(toList());
		destination = params.getDestination().toDestinationRoute(endpoint);

		try {
			for (SourceRoute source : sources)
				context.addRoutes(source);
			context.addRoutes(destination);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public EPEngine<?> getEngine() {
		return engine;
	}

	public OutputCollector getOutputCollector() {
		return collector;
	}

	public void discard() {
		try {
			for (SourceRoute source : sources)
			{
				context.stopRoute(source.getRouteId());
				context.removeRoute(source.getRouteId());
			}
			context.stopRoute(destination.getRouteId());
			context.removeRoute(destination.getRouteId());
			engine.discard();
			for (CamelConfig config : configs)
				config.removeFrom(context);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
