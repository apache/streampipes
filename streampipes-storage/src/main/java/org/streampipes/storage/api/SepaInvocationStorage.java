package org.streampipes.storage.api;

import org.streampipes.model.impl.graph.SepaInvocation;
import org.lightcouch.Response;

public interface SepaInvocationStorage {
	
	Response storeSepaInvocation(SepaInvocation sepaInvocation);
	
	SepaInvocation getSepaInvovation(String sepaInvocationId);

	boolean removeSepaInvovation(String sepaInvocationId, String sepaInvocationRev);
	
}
