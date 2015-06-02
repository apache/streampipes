package de.fzi.cep.sepa.desc;

import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;

public interface SemanticEventProcessingAgentDeclarer {

	public SepaDescription declareModel();
	
	public boolean invokeRuntime(SepaInvocation sepa);
	
	public boolean detachRuntime();
	
}
