package de.fzi.cep.sepa.actions.samples.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.JmsTransportProtocol;
import de.fzi.cep.sepa.model.impl.TransportFormat;
import de.fzi.cep.sepa.model.impl.TransportProtocol;
import de.fzi.cep.sepa.model.vocabulary.MessageFormat;

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
