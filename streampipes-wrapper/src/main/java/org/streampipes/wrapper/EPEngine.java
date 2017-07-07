package org.streampipes.wrapper;

import java.util.Map;

import org.streampipes.model.impl.graph.SepaInvocation;

public interface EPEngine<B extends BindingParameters> { // B - Bind Type

	void bind(EngineParameters<B> parameters, OutputCollector collector, SepaInvocation graph);

	void onEvent(Map<String, Object> event, String sourceInfo);

	void discard();

}
