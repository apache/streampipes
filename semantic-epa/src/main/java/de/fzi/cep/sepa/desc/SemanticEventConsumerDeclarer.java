package de.fzi.cep.sepa.desc;

import de.fzi.cep.sepa.model.impl.graph.SEC;

public interface SemanticEventConsumerDeclarer {

	public SEC declareModel();

	public boolean invokeRuntime();

	public boolean detachRuntime();
}
