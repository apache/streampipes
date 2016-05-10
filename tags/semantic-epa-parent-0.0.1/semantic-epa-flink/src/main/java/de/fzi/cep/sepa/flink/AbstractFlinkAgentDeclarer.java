package de.fzi.cep.sepa.flink;

import de.fzi.cep.sepa.desc.declarer.SemanticEventProcessingAgentDeclarer;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.runtime.param.BindingParameters;

public abstract class AbstractFlinkAgentDeclarer<B extends BindingParameters> 
	extends AbstractFlinkDeclarer<SepaDescription, SepaInvocation, FlinkSepaRuntime<B>> implements SemanticEventProcessingAgentDeclarer {
	
	
}
