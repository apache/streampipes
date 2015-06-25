package de.fzi.cep.sepa.actions.samples;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.desc.declarer.SemanticEventConsumerDeclarer;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventProperty;
import de.fzi.cep.sepa.model.impl.JmsTransportProtocol;
import de.fzi.cep.sepa.model.impl.KafkaTransportProtocol;
import de.fzi.cep.sepa.model.impl.graph.SecInvocation;


public abstract class ActionController implements SemanticEventConsumerDeclarer {

	
	protected String createWebsocketUri(SecInvocation sec)
	{
		return getEventGrounding(sec).getTransportProtocol().getBrokerHostname().replace("tcp",  "ws") + ":61614";
	}
	
	protected String extractTopic(SecInvocation sec)
	{
		return "/topic/" +getEventGrounding(sec).getTransportProtocol().getTopicName();
	}
	
	protected String createJmsUri(SecInvocation sec)
	{
		return getEventGrounding(sec).getTransportProtocol().getBrokerHostname() + ":" +((JmsTransportProtocol)getEventGrounding(sec).getTransportProtocol()).getPort();
	}
	
	protected String createKafkaUri(SecInvocation sec)
	{
		return getEventGrounding(sec).getTransportProtocol().getBrokerHostname() + ":" +((KafkaTransportProtocol)getEventGrounding(sec).getTransportProtocol()).getZookeeperPort();
	}
	
	private EventGrounding getEventGrounding(SecInvocation sec)
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
