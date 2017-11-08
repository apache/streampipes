package org.streampipes.pe.sources.samples.hella;

import org.streampipes.commons.Utils;
import org.streampipes.container.declarer.EventStreamDeclarer;
import org.streampipes.model.impl.EventGrounding;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.TransportFormat;
import org.streampipes.vocabulary.MessageFormat;
import org.streampipes.pe.sources.samples.config.ProaSenseSettings;


public abstract class AbstractHellaStream implements EventStreamDeclarer {

	public EventStream prepareStream(String topic) {
		
		EventStream stream = new EventStream();

		EventGrounding grounding = new EventGrounding();
		grounding.setTransportProtocol(ProaSenseSettings.standardProtocol(topic));
		grounding.setTransportFormats(Utils.createList(new TransportFormat(MessageFormat.Json)));
				
		stream.setEventGrounding(grounding);	
		
		return stream;
	}

	
}
