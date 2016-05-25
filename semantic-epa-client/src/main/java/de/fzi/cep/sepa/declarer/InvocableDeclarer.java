package de.fzi.cep.sepa.declarer;

import de.fzi.cep.sepa.model.InvocableSEPAElement;
import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.model.impl.Response;

public interface InvocableDeclarer<D extends NamedSEPAElement, I extends InvocableSEPAElement> extends Declarer<D> {
    public Response invokeRuntime(I invocationGraph);

    public Response detachRuntime(String pipelineId);
}
