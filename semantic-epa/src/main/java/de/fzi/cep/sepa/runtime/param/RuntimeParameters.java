package de.fzi.cep.sepa.runtime.param;

import java.util.List;
import java.util.function.Supplier;

import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.runtime.EPEngine;
import de.fzi.cep.sepa.runtime.EPRuntime;
import de.fzi.cep.sepa.runtime.OutputCollector;

public class RuntimeParameters<B extends BindingParameters> { // B - Bind Type

	private final String uri;

	private final Supplier<EPEngine<B>> supplier;
	private final EngineParameters<B> engineParameters;

	private final List<CamelConfig> configs;

	private final EndpointInfo destination;
	private final List<EndpointInfo> sources;

	public RuntimeParameters(String uri, Supplier<EPEngine<B>> supplier,
		EngineParameters<B> engineParameters, List<CamelConfig> configs, EndpointInfo destination,
		List<EndpointInfo> sources) {
		this.uri = uri;
		this.supplier = supplier;
		this.engineParameters = engineParameters;
		this.configs = configs;
		this.destination = destination;
		this.sources = sources;
	}

	public String getUri() {
		return uri;
	}

	public EPEngine<B> getPreparedEngine(EPRuntime container, SepaInvocation graph, OutputCollector collector) {
		EPEngine<B> engine = supplier.get();
		engine.bind(engineParameters, collector, graph);
		return engine;
	}

	public List<CamelConfig> getConfigs() {
		return configs;
	}

	public EndpointInfo getDestination() {
		return destination;
	}

	public List<EndpointInfo> getSources() {
		return sources;
	}

	public EngineParameters<?> getEngineParameters() {
		return engineParameters;
	}
	
}
