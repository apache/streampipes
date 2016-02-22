package de.fzi.proasense.demonstrator.sources;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.desc.declarer.EventStreamDeclarer;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.TransportFormat;
import de.fzi.cep.sepa.model.vocabulary.MessageFormat;
import de.fzi.cep.sepa.sources.AbstractAlreadyExistingStream;
import de.fzi.proasense.demonstrator.config.DemonstratorVariables;
import de.fzi.proasense.demonstrator.config.ProaSenseSettings;

public abstract class AbstractDemonstratorStream extends AbstractAlreadyExistingStream implements EventStreamDeclarer{
	protected DemonstratorVariables variables;
	
	public AbstractDemonstratorStream(DemonstratorVariables variables) {
		this.variables = variables;
	}

	public EventStream prepareStream(String topic) {
		
		EventStream stream = new EventStream();

		EventGrounding grounding = new EventGrounding();
		grounding.setTransportProtocol(ProaSenseSettings.standardProtocol(topic));
		grounding.setTransportFormats(Utils.createList(new TransportFormat(MessageFormat.Json)));
				
		stream.setEventGrounding(grounding);	
		
		return stream;
	}	

}
