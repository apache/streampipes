package org.streampipes.pe.sinks.standalone.samples.notification;

import eu.proasense.internal.RecommendationEvent;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.messaging.kafka.SpKafkaProducer;
import org.streampipes.pe.sinks.standalone.config.ActionConfig;
import org.streampipes.pe.sinks.standalone.samples.util.PlaceholderExtractor;
import org.streampipes.wrapper.runtime.EventSink;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NotificationProducer implements EventSink<NotificationParameters> {

	private SpKafkaProducer producer;
	private TSerializer serializer;
	private String title;
	private String content;

	private static final Logger LOG = LoggerFactory.getLogger(NotificationProducer.class);
	
	public NotificationProducer()
	{
		this.serializer = new TSerializer(new TBinaryProtocol.Factory());
	}

	@Override
	public void bind(NotificationParameters parameters) throws SpRuntimeException {
		this.producer = new SpKafkaProducer(ActionConfig.INSTANCE.getKafkaUrl(), "de.fzi.cep.sepa.notifications");
		this.title = parameters.getTitle();
		this.content = parameters.getContent();
	}

	@Override
	public void onEvent(Map<String, Object> event, String sourceInfo) {
		// TODO replace ProaSense Thrift format with yet-to-implement Notification model
		RecommendationEvent outEvent = new RecommendationEvent();
		outEvent.setAction(PlaceholderExtractor.replacePlaceholders(content, event));
		outEvent.setActor("Me");
		outEvent.setEventName(title);
		outEvent.setRecommendationId("Notification");
		outEvent.setEventProperties(new HashMap<>());
		outEvent.setTimestamp(new Date().getTime());

		try {
			producer.publish(serializer.serialize(outEvent));
		} catch (TException e) {
			LOG.error(e.getMessage());
		}
	}

	@Override
	public void discard() throws SpRuntimeException {
		this.producer.disconnect();
	}
}
