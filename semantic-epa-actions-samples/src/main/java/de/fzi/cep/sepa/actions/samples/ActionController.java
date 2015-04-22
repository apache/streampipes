package de.fzi.cep.sepa.actions.samples;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.desc.SemanticEventConsumerDeclarer;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventProperty;
import de.fzi.cep.sepa.model.impl.graph.SECInvocationGraph;


public abstract class ActionController implements SemanticEventConsumerDeclarer {

	
	protected String createWebsocketUri(SECInvocationGraph sec)
	{
		return getEventGrounding(sec).getUri().replace("tcp",  "ws") + ":61614";
	}
	
	protected String extractTopic(SECInvocationGraph sec)
	{
		return "/topic/" +getEventGrounding(sec).getTopicName();
	}
	
	protected String createJmsUri(SECInvocationGraph sec)
	{
		return getEventGrounding(sec).getUri() + ":" +getEventGrounding(sec).getPort();
	}
	
	private EventGrounding getEventGrounding(SECInvocationGraph sec)
	{
		return sec.getInputStreams().get(0).getEventGrounding();
	}
	
	protected String[] getColumnNames(List<EventProperty> eventProperties)
	{
		List<String> result = new ArrayList<>();
		for(EventProperty p : eventProperties)
		{
			result.add(p.getRuntimeName());
		}
		return result.toArray(new String[0]);
	}
}
