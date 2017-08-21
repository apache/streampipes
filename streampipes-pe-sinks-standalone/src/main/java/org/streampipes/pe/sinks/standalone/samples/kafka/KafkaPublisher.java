package org.streampipes.pe.sinks.standalone.samples.kafka;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.dataformat.json.JsonDataFormatDefinition;
import org.streampipes.messaging.kafka.SpKafkaProducer;
import org.streampipes.wrapper.runtime.EventSink;

import java.util.Map;

public class KafkaPublisher implements EventSink<KafkaParameters> {

	private SpKafkaProducer producer;
	private JsonDataFormatDefinition dataFormatDefinition;

	public KafkaPublisher() {
		this.dataFormatDefinition = new JsonDataFormatDefinition();
	}

	@Override
	public void bind(KafkaParameters parameters) throws SpRuntimeException {
		this.producer = new SpKafkaProducer(parameters.getKafkaHost() +":" +parameters.getKafkaPort(), parameters
						.getTopic());
	}

	@Override
	public void onEvent(Map<String, Object> event, String sourceInfo) {
		try {
			producer.publish(dataFormatDefinition.fromMap(event));
		} catch (SpRuntimeException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void discard() throws SpRuntimeException {
		this.producer.disconnect();
	}
}
