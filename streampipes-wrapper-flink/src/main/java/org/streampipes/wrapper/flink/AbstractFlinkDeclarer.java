package org.streampipes.wrapper.flink;

import org.streampipes.container.declarer.InvocableDeclarer;
import org.streampipes.commons.config.old.ClientConfiguration;
import org.streampipes.model.InvocableSEPAElement;
import org.streampipes.model.NamedSEPAElement;
import org.streampipes.model.impl.Response;

public abstract class AbstractFlinkDeclarer<D extends NamedSEPAElement, I extends InvocableSEPAElement, ER extends FlinkRuntime<I>> implements InvocableDeclarer<D, I> {

	protected I graph;
	protected ER runtime;
	
	@Override
	public Response invokeRuntime(I graph)
	{
		runtime = getRuntime(graph);
		this.graph = graph;
		
		if (runtime.startExecution()) {
			return new Response(graph.getElementId(), true);
		}
		else {
			return new Response(graph.getElementId(), false);
		}
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
