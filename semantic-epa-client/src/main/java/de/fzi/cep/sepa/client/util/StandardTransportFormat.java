package de.fzi.cep.sepa.client.util;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.JmsTransportProtocol;
import de.fzi.cep.sepa.model.impl.KafkaTransportProtocol;
import de.fzi.cep.sepa.model.impl.TransportFormat;
import de.fzi.cep.sepa.model.impl.TransportProtocol;
import de.fzi.cep.sepa.model.vocabulary.MessageFormat;

public class StandardTransportFormat {

	private static List<TransportFormat> standardFormat()
	{
		List<TransportFormat> formats = new ArrayList<TransportFormat>();
		formats.add(new TransportFormat(MessageFormat.Json));
		formats.add(new TransportFormat(MessageFormat.Thrift));
		return formats;
	}
	
	private static List<TransportProtocol> standardProtocols()
	{
		List<TransportProtocol> protocols = new ArrayList<TransportProtocol>();
		protocols.add(new JmsTransportProtocol());
		protocols.add(new KafkaTransportProtocol());
		return protocols;
	}
	
	
	public static EventGrounding getSupportedGrounding()
	{
		EventGrounding grounding = new EventGrounding();
		grounding.setTransportFormats(standardFormat());
		grounding.setTransportProtocols(standardProtocols());
		return grounding;
	}
}
