package de.fzi.cep.sepa.client.declarer;

import de.fzi.cep.sepa.model.InvocableSEPAElement;
import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.model.impl.Response;

public interface InvocableDeclarer<D extends NamedSEPAElement, I extends InvocableSEPAElement> extends Declarer<D> {

    Response invokeRuntime(I invocationGraph);

    Response detachRuntime(String pipelineId);
}
