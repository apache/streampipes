package de.fzi.cep.sepa.actions.samples.jms;

import de.fzi.cep.sepa.desc.SemanticEventConsumerDeclarer;
import de.fzi.cep.sepa.model.impl.graph.SEC;

public class JMSConsumer implements SemanticEventConsumerDeclarer{

	@Override
	public SEC declareModel() {
		SEC sec = new SEC("/jms", "JMS Consumer", "Desc", "http://localhost:8080/img");
		return sec;
	}

	@Override
	public boolean invokeRuntime() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean detachRuntime() {
		// TODO Auto-generated method stub
		return false;
	}

}
