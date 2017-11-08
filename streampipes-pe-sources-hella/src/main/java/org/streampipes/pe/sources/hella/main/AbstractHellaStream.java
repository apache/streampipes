package org.streampipes.pe.sources.hella.main;

import org.streampipes.commons.Utils;
import org.streampipes.container.declarer.EventStreamDeclarer;
import org.streampipes.model.impl.EventGrounding;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.TransportFormat;
import org.streampipes.vocabulary.MessageFormat;
import org.streampipes.sources.AbstractAlreadyExistingStream;
import org.streampipes.pe.sources.hella.config.ProaSenseSettings;

public abstract class AbstractHellaStream extends AbstractAlreadyExistingStream implements EventStreamDeclarer {

	public EventStream prepareStream(String topic) {
		
		EventStream stream = new EventStream();

		EventGrounding grounding = new EventGrounding();
		grounding.setTransportProtocol(ProaSenseSettings.standardProtocol(topic));
		grounding.setTransportFormats(Utils.createList(new TransportFormat(MessageFormat.Json)));
				
		stream.setEventGrounding(grounding);	
		
		return stream;
	}
	
}
