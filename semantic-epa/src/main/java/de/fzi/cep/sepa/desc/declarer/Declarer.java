package de.fzi.cep.sepa.desc.declarer;

import de.fzi.cep.sepa.model.InvocableSEPAElement;
import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.model.impl.Response;

public interface Declarer<D extends NamedSEPAElement, I extends InvocableSEPAElement> {

	public D declareModel();
	
	public Response invokeRuntime(I invocationGraph);
	
	public Response detachRuntime();
}
