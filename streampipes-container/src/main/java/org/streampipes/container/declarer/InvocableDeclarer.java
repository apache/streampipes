package org.streampipes.container.declarer;

import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.model.base.NamedStreamPipesEntity;
import org.streampipes.model.Response;

public interface InvocableDeclarer<D extends NamedStreamPipesEntity, I extends InvocableStreamPipesEntity> extends Declarer<D> {

    Response invokeRuntime(I invocationGraph);

    Response detachRuntime(String pipelineId);
}
