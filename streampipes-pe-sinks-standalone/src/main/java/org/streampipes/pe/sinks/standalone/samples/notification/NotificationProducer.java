package org.streampipes.pe.sinks.standalone.samples.notification;

import eu.proasense.internal.RecommendationEvent;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.streampipes.messaging.EventConsumer;
import org.streampipes.messaging.kafka.SpKafkaProducer;
import org.streampipes.model.impl.graph.SecInvocation;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.pe.sinks.standalone.config.ActionConfig;
import org.streampipes.pe.sinks.standalone.samples.util.PlaceholderExtractor;

import java.util.Date;
import java.util.HashMap;

public class NotificationProducer implements EventConsumer<byte[]> {

	SpKafkaProducer producer;
	private TSerializer serializer;
	private String title;
	private String content;

	
	public NotificationProducer(SecInvocation sec)
	{
		producer = new SpKafkaProducer(ActionConfig.INSTANCE.getKafkaUrl(), "de.fzi.cep.sepa.notifications");
		this.title = SepaUtils.getFreeTextStaticPropertyValue(sec, "title");
		this.content = SepaUtils.getFreeTextStaticPropertyValue(sec, "content");
		this.serializer = new TSerializer(new TBinaryProtocol.Factory());
	}
	
	@Override
	public void onEvent(byte[] json) {
		RecommendationEvent event = new RecommendationEvent();
		event.setAction(PlaceholderExtractor.replacePlaceholders(content, new String(json)));
		event.setActor("Me");
		event.setEventName(title);
		event.setRecommendationId("Notification");
		event.setEventProperties(new HashMap<>());
		event.setTimestamp(new Date().getTime());
		
		try {
			producer.publish(serializer.serialize(event));
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
