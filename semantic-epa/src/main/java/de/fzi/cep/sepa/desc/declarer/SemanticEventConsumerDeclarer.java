package de.fzi.cep.sepa.desc.declarer;

import de.fzi.cep.sepa.model.impl.graph.SecDescription;
import de.fzi.cep.sepa.model.impl.graph.SecInvocation;

public interface SemanticEventConsumerDeclarer extends RuntimeDeclarer<SecDescription, SecInvocation> {

	public boolean isVisualizable();
	
	public String getHtml(SecInvocation graph);
}
