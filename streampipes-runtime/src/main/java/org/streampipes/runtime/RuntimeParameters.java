package org.streampipes.runtime;

import java.util.function.Supplier;

import org.streampipes.model.impl.graph.SepaInvocation;

public abstract class RuntimeParameters<B extends BindingParameters> { // B - Bind Type

	private final String uri;

	private final Supplier<EPEngine<B>> supplier;
	private final EngineParameters<B> engineParameters;

	public RuntimeParameters(String uri, Supplier<EPEngine<B>> supplier,
		EngineParameters<B> engineParameters) {
		this.uri = uri;
		this.supplier = supplier;
		this.engineParameters = engineParameters;

	}

	public String getUri() {
		return uri;
	}

	public EPEngine<B> getPreparedEngine(EPRuntime container, SepaInvocation graph, OutputCollector collector) {
		EPEngine<B> engine = supplier.get();
		engine.bind(engineParameters, collector, graph);
		return engine;
	}

	public EngineParameters<?> getEngineParameters() {
		return engineParameters;
	}
	
}
