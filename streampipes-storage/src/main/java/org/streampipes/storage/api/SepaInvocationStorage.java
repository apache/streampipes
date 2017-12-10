package org.streampipes.storage.api;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.lightcouch.Response;

public interface SepaInvocationStorage {
	
	Response storeSepaInvocation(DataProcessorInvocation dataProcessorInvocation);
	
	DataProcessorInvocation getSepaInvovation(String sepaInvocationId);

	boolean removeSepaInvovation(String sepaInvocationId, String sepaInvocationRev);
	
}
