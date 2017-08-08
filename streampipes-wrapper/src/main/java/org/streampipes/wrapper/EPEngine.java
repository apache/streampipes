package org.streampipes.wrapper;

import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.wrapper.params.BindingParameters;
import org.streampipes.wrapper.params.EngineParameters;

import java.util.Map;

public interface EPEngine<B extends BindingParameters> { // B - Bind Type

	void bind(EngineParameters<B> parameters, OutputCollector collector, SepaInvocation graph);

	void onEvent(Map<String, Object> event, String sourceInfo);

	void discard();

}
