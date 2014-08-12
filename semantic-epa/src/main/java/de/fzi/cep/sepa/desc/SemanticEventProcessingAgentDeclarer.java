package de.fzi.cep.sepa.desc;

import de.fzi.cep.sepa.model.impl.SEPA;

public interface SemanticEventProcessingAgentDeclarer {

	public SEPA declareModel();
	
	public boolean invokeRuntime(SEPA sepa);
	
	public boolean detachRuntime(SEPA sepa);
	
}
