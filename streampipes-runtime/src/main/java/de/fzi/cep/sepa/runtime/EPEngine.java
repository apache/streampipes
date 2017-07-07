package de.fzi.cep.sepa.runtime;

import java.util.Map;

import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.runtime.param.BindingParameters;
import de.fzi.cep.sepa.runtime.param.EngineParameters;

public interface EPEngine<B extends BindingParameters> { // B - Bind Type

	void bind(EngineParameters<B> parameters, OutputCollector collector, SepaInvocation graph);

	void onEvent(Map<String, Object> event, String sourceInfo);

	void discard();

}
