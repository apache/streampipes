package org.streampipes.pe.sinks.standalone.samples.jms;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.dataformat.json.JsonDataFormatDefinition;
import org.streampipes.messaging.jms.ActiveMQPublisher;
import org.streampipes.wrapper.runtime.EventSink;

import java.util.Map;

public class JmsPublisher implements EventSink<JmsParameters> {

	private ActiveMQPublisher publisher;
	private JsonDataFormatDefinition jsonDataFormatDefinition;
	
	public JmsPublisher() {
			this.jsonDataFormatDefinition = new JsonDataFormatDefinition();
	}

	@Override
	public void bind(JmsParameters params) throws SpRuntimeException {
		this.publisher = new ActiveMQPublisher(params.getJmsHost() +":" +params.getJmsPort(), params.getTopic());
	}

	@Override
	public void onEvent(Map<String, Object> event, String sourceInfo) {
		try {
			this.publisher.publish(jsonDataFormatDefinition.fromMap(event));
		} catch (SpRuntimeException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void discard() throws SpRuntimeException {
		this.publisher.disconnect();
	}
}
