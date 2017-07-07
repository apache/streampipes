package de.fzi.cep.sepa.flink;

import de.fzi.cep.sepa.client.declarer.SemanticEventConsumerDeclarer;
import de.fzi.cep.sepa.model.impl.graph.SecDescription;
import de.fzi.cep.sepa.model.impl.graph.SecInvocation;

public abstract class AbstractFlinkConsumerDeclarer 
	extends AbstractFlinkDeclarer<SecDescription, SecInvocation, FlinkSecRuntime> implements SemanticEventConsumerDeclarer {

}
