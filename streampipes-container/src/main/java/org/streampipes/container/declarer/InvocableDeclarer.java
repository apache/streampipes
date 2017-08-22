package org.streampipes.container.declarer;

import org.streampipes.model.InvocableSEPAElement;
import org.streampipes.model.NamedSEPAElement;
import org.streampipes.model.impl.Response;

public interface InvocableDeclarer<D extends NamedSEPAElement, I extends InvocableSEPAElement> extends Declarer<D> {

    Response invokeRuntime(I invocationGraph);

    Response detachRuntime(String pipelineId);
}
