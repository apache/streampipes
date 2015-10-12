package de.fzi.cep.sepa.storage.api;

import org.lightcouch.Response;

import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;

public interface SepaInvocationStorage {
	
	public Response storeSepaInvocation(SepaInvocation sepaInvocation);
	
	public SepaInvocation getSepaInvovation(String sepaInvocationId);

	public boolean removeSepaInvovation(String sepaInvocationId, String sepaInvocationRev);
	
}
