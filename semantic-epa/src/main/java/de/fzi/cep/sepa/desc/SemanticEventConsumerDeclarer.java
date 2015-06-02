package de.fzi.cep.sepa.desc;

import de.fzi.cep.sepa.model.impl.graph.SecDescription;
import de.fzi.cep.sepa.model.impl.graph.SecInvocation;

public interface SemanticEventConsumerDeclarer {

	public SecDescription declareModel();

	public String invokeRuntime(SecInvocation sec);

	public boolean detachRuntime(SecInvocation sec);
}
