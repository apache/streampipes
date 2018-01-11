package org.streampipes.wrapper.declarer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.container.declarer.SemanticEventProcessingAgentDeclarer;
import org.streampipes.model.Response;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;
import org.streampipes.wrapper.runtime.PipelineElementRuntime;

public abstract class EventProcessorDeclarer<B extends EventProcessorBindingParams, EPR extends
				PipelineElementRuntime> extends PipelineElementDeclarer<B, EPR, DataProcessorInvocation,
				ProcessingElementParameterExtractor> implements
				SemanticEventProcessingAgentDeclarer {

	public static final Logger logger = LoggerFactory.getLogger(EventProcessorDeclarer.class.getCanonicalName());

	@Override
	protected ProcessingElementParameterExtractor getExtractor(DataProcessorInvocation graph) {
		return ProcessingElementParameterExtractor.from(graph);
	}

	@Override
	public Response invokeRuntime(DataProcessorInvocation graph) {
		return invokeEPRuntime(graph);
	}

}
