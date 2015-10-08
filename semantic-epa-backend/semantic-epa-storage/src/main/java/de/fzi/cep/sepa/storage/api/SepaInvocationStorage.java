package de.fzi.cep.sepa.storage.api;

import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;

public interface SepaInvocationStorage {
	
	public String storeSepaInvocation(SepaInvocation sepaInvocation);
	
	public SepaInvocation getSepaInvovation(String sepaInvocationId);
	
}
