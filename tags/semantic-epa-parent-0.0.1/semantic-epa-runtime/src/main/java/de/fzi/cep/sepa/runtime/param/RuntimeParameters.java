package de.fzi.cep.sepa.runtime.param;

import java.util.function.Supplier;

import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.runtime.EPEngine;
import de.fzi.cep.sepa.runtime.EPRuntime;
import de.fzi.cep.sepa.runtime.OutputCollector;

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
