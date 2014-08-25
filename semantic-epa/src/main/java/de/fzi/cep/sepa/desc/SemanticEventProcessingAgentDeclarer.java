package de.fzi.cep.sepa.desc;

import de.fzi.cep.sepa.model.impl.graph.SEPA;
import de.fzi.cep.sepa.model.impl.graph.SEPAInvocationGraph;

public interface SemanticEventProcessingAgentDeclarer {

	public SEPA declareModel();
	
	public boolean invokeRuntime(SEPAInvocationGraph sepa);
	
	public boolean detachRuntime(SEPAInvocationGraph sepa);
	
}
