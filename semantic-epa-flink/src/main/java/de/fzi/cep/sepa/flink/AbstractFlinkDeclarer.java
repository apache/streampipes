package de.fzi.cep.sepa.flink;

import de.fzi.cep.sepa.declarer.InvocableDeclarer;
import de.fzi.cep.sepa.model.InvocableSEPAElement;
import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.model.impl.Response;

public abstract class AbstractFlinkDeclarer<D extends NamedSEPAElement, I extends InvocableSEPAElement, ER extends FlinkRuntime<I>> implements InvocableDeclarer<D, I> {

	protected I graph;
	protected ER runtime;
	
	@Override
	public Response invokeRuntime(I graph)
	{
		runtime = getRuntime(graph);
		this.graph = graph;
		
		if (runtime.startExecution())
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
	
	protected abstract ER getRuntime(I graph);
	
}
