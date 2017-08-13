package org.streampipes.wrapper.declarer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.container.declarer.SemanticEventProcessingAgentDeclarer;
import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;
import org.streampipes.wrapper.runtime.EventProcessorRuntime;
import org.streampipes.wrapper.runtime.EventProcessor;

import java.util.function.Supplier;

public abstract class EventProcessorDeclarer<B extends EventProcessorBindingParams, EPR extends
				EventProcessorRuntime> extends PipelineElementDeclarer<B, EPR, SepaInvocation,
				ProcessingElementParameterExtractor, EventProcessor<B>> implements
				SemanticEventProcessingAgentDeclarer {

	public static final Logger logger = LoggerFactory.getLogger(EventProcessorDeclarer.class.getCanonicalName());
		
	public void invokeEPRuntime(B bindingParameters, Supplier<EventProcessor<B>> supplier) throws Exception {

		elementId = bindingParameters.getGraph().getElementId();

		epRuntime = prepareRuntime(bindingParameters, supplier);
		epRuntime.initRuntime();
	}

	@Override
	protected ProcessingElementParameterExtractor getExtractor(SepaInvocation graph) {
		return ProcessingElementParameterExtractor.from(graph);
	}

	public abstract EPR prepareRuntime(B bindingParameters, Supplier<EventProcessor<B>> supplier);

}
