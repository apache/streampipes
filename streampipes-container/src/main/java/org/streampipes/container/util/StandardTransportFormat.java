package org.streampipes.container.util;

import org.streampipes.model.grounding.EventGrounding;
import org.streampipes.model.grounding.JmsTransportProtocol;
import org.streampipes.model.grounding.KafkaTransportProtocol;
import org.streampipes.model.grounding.TransportFormat;
import org.streampipes.model.grounding.TransportProtocol;
import org.streampipes.vocabulary.MessageFormat;

import java.util.ArrayList;
import java.util.List;

public class StandardTransportFormat {

	public static List<TransportFormat> standardFormat()
	{
		List<TransportFormat> formats = new ArrayList<TransportFormat>();
		formats.add(new TransportFormat(MessageFormat.Json));
		formats.add(new TransportFormat(MessageFormat.Thrift));
		return formats;
	}
	
	public static List<TransportProtocol> standardProtocols()
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
