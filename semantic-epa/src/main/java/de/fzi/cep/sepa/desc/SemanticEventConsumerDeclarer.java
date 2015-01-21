package de.fzi.cep.sepa.desc;

import de.fzi.cep.sepa.model.impl.graph.SEC;
import de.fzi.cep.sepa.model.impl.graph.SECInvocationGraph;

public interface SemanticEventConsumerDeclarer {

	public SEC declareModel();

	public String invokeRuntime(SECInvocationGraph sec);

	public boolean detachRuntime(SECInvocationGraph sec);
}
