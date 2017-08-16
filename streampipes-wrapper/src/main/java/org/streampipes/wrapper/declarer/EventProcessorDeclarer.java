package org.streampipes.wrapper.declarer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.container.declarer.SemanticEventProcessingAgentDeclarer;
import org.streampipes.model.impl.Response;
import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.wrapper.ConfiguredEventProcessor;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;
import org.streampipes.wrapper.runtime.EventProcessor;
import org.streampipes.wrapper.runtime.EventProcessorRuntime;

public abstract class EventProcessorDeclarer<B extends EventProcessorBindingParams, EPR extends
				EventProcessorRuntime> extends PipelineElementDeclarer<B, EPR, SepaInvocation,
				ProcessingElementParameterExtractor, EventProcessor<B>> implements
				SemanticEventProcessingAgentDeclarer {

	public static final Logger logger = LoggerFactory.getLogger(EventProcessorDeclarer.class.getCanonicalName());

	@Override
	protected ProcessingElementParameterExtractor getExtractor(SepaInvocation graph) {
		return ProcessingElementParameterExtractor.from(graph);
	}

	@Override
	public Response invokeRuntime(SepaInvocation graph) {
		ConfiguredEventProcessor<B, EventProcessor<B>> configuredEngine = onInvocation(graph);

		return submit(configuredEngine.getBindingParams(), configuredEngine.getEngineSupplier());
	}

	public abstract ConfiguredEventProcessor<B, EventProcessor<B>> onInvocation(SepaInvocation
																																											 graph);


}
