package de.fzi.cep.sepa.actions.samples.notification;

import java.util.Date;
import java.util.HashMap;

import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;

import de.fzi.cep.sepa.commons.config.ClientConfiguration;
import de.fzi.cep.sepa.commons.messaging.IMessageListener;
import de.fzi.cep.sepa.commons.messaging.ProaSenseInternalProducer;
import de.fzi.cep.sepa.model.impl.graph.SecInvocation;
import de.fzi.cep.sepa.model.util.SepaUtils;
import eu.proasense.internal.RecommendationEvent;

public class NotificationProducer implements IMessageListener {

	ProaSenseInternalProducer producer;
	private TSerializer serializer;
	private String title;
	private String content;
	
	public NotificationProducer(SecInvocation sec)
	{
		producer = new ProaSenseInternalProducer(ClientConfiguration.INSTANCE.getKafkaUrl(), "de.fzi.cep.sepa.notifications");
		this.title = SepaUtils.getFreeTextStaticPropertyValue(sec, "title");
		this.content = SepaUtils.getFreeTextStaticPropertyValue(sec, "content");
		this.serializer = new TSerializer(new TBinaryProtocol.Factory());
	}
	
	@Override
	public void onEvent(String json) {
		RecommendationEvent event = new RecommendationEvent();
		event.setAction(content);
		event.setActor("");
		event.setEventName(title);
		event.setRecommendationId("Notification");
		event.setEventProperties(new HashMap<>());
		event.setTimestamp(new Date().getTime());
		
		try {
			producer.send(serializer.serialize(event));
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
