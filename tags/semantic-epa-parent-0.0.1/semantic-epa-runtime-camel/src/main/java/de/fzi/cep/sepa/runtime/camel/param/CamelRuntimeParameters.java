package de.fzi.cep.sepa.runtime.camel.param;

import java.util.List;
import java.util.function.Supplier;

import de.fzi.cep.sepa.runtime.EPEngine;
import de.fzi.cep.sepa.runtime.param.BindingParameters;
import de.fzi.cep.sepa.runtime.param.EngineParameters;
import de.fzi.cep.sepa.runtime.param.RuntimeParameters;

public class CamelRuntimeParameters<B extends BindingParameters> extends RuntimeParameters<B> { // B - Bind Type

	private final List<CamelConfig> configs;

	private final EndpointInfo destination;
	private final List<EndpointInfo> sources;

	public CamelRuntimeParameters(String uri, Supplier<EPEngine<B>> supplier,
		EngineParameters<B> engineParameters, List<CamelConfig> configs, EndpointInfo destination,
		List<EndpointInfo> sources) {
		super(uri, supplier, engineParameters);
		this.configs = configs;
		this.destination = destination;
		this.sources = sources;
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

}
