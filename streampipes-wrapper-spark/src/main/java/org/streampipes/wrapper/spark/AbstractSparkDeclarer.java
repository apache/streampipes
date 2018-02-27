package org.streampipes.wrapper.spark;

import org.streampipes.container.declarer.InvocableDeclarer;
import org.streampipes.model.Response;
import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.model.base.NamedStreamPipesEntity;
import org.streampipes.model.graph.DataProcessorInvocation;

/**
 * Created by Jochen Lutz on 2017-11-28.
 */
public abstract class AbstractSparkDeclarer<D extends NamedStreamPipesEntity, I extends InvocableStreamPipesEntity, SR extends SparkRuntime> implements InvocableDeclarer<D, I> {
    protected SR runtime;
    protected I graph;

    @Override
    public Response invokeRuntime(I sepaInvocation) {
        runtime = getRuntime(sepaInvocation);
        graph = sepaInvocation;

        if (runtime.startExecution()) {
            return new Response(graph.getElementId(), true);
        }
        else {
            return new Response(graph.getElementId(), false);
        }
    }

    @Override
    public Response detachRuntime(String s) {
        if (runtime.stop()) {
            return new Response(graph.getElementId(), true);
        }
        else {
            return new Response(graph.getElementId(), false);
        }
    }

    protected abstract SR getRuntime(I graph);
}
