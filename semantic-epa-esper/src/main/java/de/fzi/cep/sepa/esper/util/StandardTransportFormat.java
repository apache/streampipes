package de.fzi.cep.sepa.esper.util;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.TransportFormat;
import de.fzi.cep.sepa.model.vocabulary.MessageFormat;

public class StandardTransportFormat {

	private static List<TransportFormat> standardFormat()
	{
		List<TransportFormat> formats = new ArrayList<TransportFormat>();
		formats.add(new TransportFormat(MessageFormat.Json));
		formats.add(new TransportFormat(MessageFormat.Thrift));
		return formats;
	}
	
	
	public static EventGrounding getSupportedGrounding()
	{
		EventGrounding grounding = new EventGrounding();
		grounding.setTransportFormats(standardFormat());
		return grounding;
	}
}
