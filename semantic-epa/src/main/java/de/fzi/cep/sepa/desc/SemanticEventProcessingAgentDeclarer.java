package de.fzi.cep.sepa.desc;

import de.fzi.cep.sepa.model.impl.SEPA;
import de.fzi.cep.sepa.model.impl.SEPAInvocationGraph;

public interface SemanticEventProcessingAgentDeclarer {

	public SEPA declareModel();
	
	public boolean invokeRuntime(SEPAInvocationGraph sepa);
	
	public boolean detachRuntime(SEPA sepa);
	
}
