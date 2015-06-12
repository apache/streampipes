package de.fzi.cep.sepa.desc.declarer;

import de.fzi.cep.sepa.model.InvocableSEPAElement;
import de.fzi.cep.sepa.model.NamedSEPAElement;

public interface Declarer<D extends NamedSEPAElement, I extends InvocableSEPAElement> {

	public D declareModel();
	
	public boolean invokeRuntime(I invocationGraph);
	
	public boolean detachRuntime();
}
