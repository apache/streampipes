package org.streampipes.pe.sinks.standalone.samples.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.streampipes.model.impl.EventGrounding;
import org.streampipes.model.impl.JmsTransportProtocol;
import org.streampipes.model.impl.TransportFormat;
import org.streampipes.model.impl.TransportProtocol;
import org.streampipes.model.vocabulary.MessageFormat;

public class ActionUtils {

	private static List<TransportProtocol> standardProtocols()
	{
		List<TransportProtocol> protocols = new ArrayList<TransportProtocol>();
		protocols.add(new JmsTransportProtocol());
		return protocols;
	}
	
	
	public static EventGrounding getSupportedGrounding()
	{
		EventGrounding grounding = new EventGrounding();
		grounding.setTransportFormats(standardFormat());
		grounding.setTransportProtocols(standardProtocols());
		return grounding;
	}


	private static List<TransportFormat> standardFormat() {
		return Arrays.asList(new TransportFormat(MessageFormat.Json));
	}
}
