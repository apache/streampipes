package org.streampipes.pe.sources.demonstrator.sources;

import org.streampipes.commons.Utils;
import org.streampipes.container.declarer.EventStreamDeclarer;
import org.streampipes.model.grounding.EventGrounding;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.grounding.TransportFormat;
import org.streampipes.vocabulary.MessageFormat;
import org.streampipes.pe.sources.demonstrator.config.DemonstratorVariables;
import org.streampipes.sources.AbstractAlreadyExistingStream;
import org.streampipes.pe.sources.demonstrator.config.ProaSenseSettings;

public abstract class AbstractDemonstratorStream extends AbstractAlreadyExistingStream implements EventStreamDeclarer{
	protected DemonstratorVariables variables;
	
	public AbstractDemonstratorStream(DemonstratorVariables variables) {
		this.variables = variables;
	}

	public SpDataStream prepareStream(String topic) {
		
		SpDataStream stream = new SpDataStream();

		EventGrounding grounding = new EventGrounding();
		grounding.setTransportProtocol(ProaSenseSettings.standardProtocol(topic));
		grounding.setTransportFormats(Utils.createList(new TransportFormat(MessageFormat.Json)));
				
		stream.setEventGrounding(grounding);	
		
		return stream;
	}	

}
