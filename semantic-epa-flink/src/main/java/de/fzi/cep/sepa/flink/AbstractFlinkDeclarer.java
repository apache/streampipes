package de.fzi.cep.sepa.flink;

import de.fzi.cep.sepa.desc.declarer.SemanticEventProcessingAgentDeclarer;
import de.fzi.cep.sepa.model.impl.Response;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.runtime.param.BindingParameters;

public abstract class AbstractFlinkDeclarer<B extends BindingParameters> implements SemanticEventProcessingAgentDeclarer {

	private FlinkSepaRuntime<B, ?> runtime;
	private SepaInvocation graph;
	
	public Response invokeRuntime(SepaInvocation graph)
	{
		runtime = getRuntime(graph);
		
		if (runtime.execute())
			return new Response(graph.getElementId(), true);
		else
			return new Response(graph.getElementId(), false); 
	}
	
	@Override
	public Response detachRuntime(String pipelineId) {
		if (runtime.stop())
			return new Response(graph.getElementId(), true);
		else
			return new Response(graph.getElementId(), false);
	}
	
	protected abstract FlinkSepaRuntime<B, ?> getRuntime(SepaInvocation graph);
}
