package de.fzi.cep.sepa.actions.samples;

import de.fzi.cep.sepa.desc.SemanticEventConsumerDeclarer;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.graph.SECInvocationGraph;


public abstract class ActionController implements SemanticEventConsumerDeclarer {

	
	protected String createWebsocketUri(SECInvocationGraph sec)
	{
		return getEventGrounding(sec).getUri().replace("tcp",  "ws") + ":61614";
	}
	
	protected String extractTopic(SECInvocationGraph sec)
	{
		return getEventGrounding(sec).getTopicName();
	}
	
	private EventGrounding getEventGrounding(SECInvocationGraph sec)
	{
		return sec.getInputStreams().get(0).getEventGrounding();
	}
}
