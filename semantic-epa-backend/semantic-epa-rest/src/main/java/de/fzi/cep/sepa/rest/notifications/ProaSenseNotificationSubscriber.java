package de.fzi.cep.sepa.rest.notifications;

import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;

import de.fzi.cep.sepa.commons.config.Configuration;
import de.fzi.cep.sepa.commons.messaging.IMessageListener;
import de.fzi.cep.sepa.commons.messaging.kafka.KafkaConsumerGroup;
import de.fzi.cep.sepa.messages.ProaSenseNotificationMessage;
import de.fzi.cep.sepa.storage.controller.StorageManager;
import eu.proasense.internal.RecommendationEvent;

public class ProaSenseNotificationSubscriber implements IMessageListener {

	
	private TDeserializer deserializer;
	
	public ProaSenseNotificationSubscriber() {
		this.deserializer = new TDeserializer(new TBinaryProtocol.Factory());
		
	}
	
	public void subscribe(String topic)
	{
		KafkaConsumerGroup kafkaConsumerGroup = new KafkaConsumerGroup(Configuration.getInstance().getBrokerConfig().getZookeeperUrl(), topic,
				new String[] {topic}, this);
		kafkaConsumerGroup.run(1);
	}

	@Override
	public void onEvent(String json) {
		RecommendationEvent event = new RecommendationEvent();
		System.out.println("event");
		try {
			deserializer.deserialize(event,  json.getBytes());
			StorageManager.INSTANCE.getNotificationStorageApi().addNotification(new ProaSenseNotificationMessage(event.getEventName(), event.getTimestamp(), event.getAction(), event.getActor()));
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			}
}
