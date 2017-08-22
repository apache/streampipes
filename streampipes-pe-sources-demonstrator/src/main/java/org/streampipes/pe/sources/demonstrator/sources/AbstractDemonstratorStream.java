package org.streampipes.pe.sources.demonstrator.sources;

import org.streampipes.commons.Utils;
import org.streampipes.container.declarer.EventStreamDeclarer;
import org.streampipes.model.impl.EventGrounding;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.TransportFormat;
import org.streampipes.model.vocabulary.MessageFormat;
import org.streampipes.pe.sources.demonstrator.config.DemonstratorVariables;
import org.streampipes.sources.AbstractAlreadyExistingStream;
import org.streampipes.pe.sources.demonstrator.config.ProaSenseSettings;

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
