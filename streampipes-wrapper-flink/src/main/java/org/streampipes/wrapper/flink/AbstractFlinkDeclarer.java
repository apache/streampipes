package org.streampipes.wrapper.flink;

import org.streampipes.container.declarer.InvocableDeclarer;
import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.model.base.NamedStreamPipesEntity;
import org.streampipes.model.Response;

public abstract class AbstractFlinkDeclarer<D extends NamedStreamPipesEntity, I extends InvocableStreamPipesEntity, ER extends FlinkRuntime<I>> implements InvocableDeclarer<D, I> {

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
