package de.fzi.cep.sepa.storage.api;

import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import org.lightcouch.Response;

public interface SepaInvocationStorage {
	
	Response storeSepaInvocation(SepaInvocation sepaInvocation);
	
	SepaInvocation getSepaInvovation(String sepaInvocationId);

	boolean removeSepaInvovation(String sepaInvocationId, String sepaInvocationRev);
	
}
