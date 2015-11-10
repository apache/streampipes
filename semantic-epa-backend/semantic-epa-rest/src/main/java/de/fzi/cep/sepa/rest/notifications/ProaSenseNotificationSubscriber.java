package de.fzi.cep.sepa.rest.notifications;

import java.text.SimpleDateFormat;

import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;

import de.fzi.cep.sepa.commons.config.Configuration;
import de.fzi.cep.sepa.commons.messaging.IMessageListener;
import de.fzi.cep.sepa.commons.messaging.kafka.KafkaConsumerGroup;
import de.fzi.cep.sepa.messages.ProaSenseNotificationMessage;
import de.fzi.cep.sepa.storage.controller.StorageManager;
import eu.proasense.internal.RecommendationEvent;

public class ProaSenseNotificationSubscriber implements IMessageListener, Runnable {

	
	private TDeserializer deserializer;
	private String topic;
	
	public ProaSenseNotificationSubscriber(String topic) {
		this.deserializer = new TDeserializer(new TBinaryProtocol.Factory());
		this.topic = topic;
	}
	
	public void subscribe()
	{
		KafkaConsumerGroup kafkaConsumerGroup = new KafkaConsumerGroup(Configuration.getInstance().getBrokerConfig().getZookeeperUrl(), topic,
				new String[] {topic}, this);
		kafkaConsumerGroup.run(1);
	}

	@Override
	public void onEvent(String json) {
		RecommendationEvent event = new RecommendationEvent();
		String recommendedDate = "";
		try {
			deserializer.deserialize(event,  json.getBytes());
			if (event.getEventProperties().containsKey("action_timestamp")) recommendedDate += " at time " +parseDate(Long.parseLong(event.getEventProperties().get("action_timestamp").getValue())) +"(Recommendation ID: " +event.getRecommendationId() +", Timestamp: " +event.getTimestamp()+", " +"Action_Timestamp" +event.getEventProperties().get("action_timestamp") +")";
			StorageManager.INSTANCE.getNotificationStorageApi().addNotification(new ProaSenseNotificationMessage(event.getEventName(), event.getTimestamp(), event.getAction() +recommendedDate, event.getActor()));
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			}

	private String parseDate(long timestamp)
	{
		return new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(timestamp);
	}
	
	@Override
	public void run() {
		subscribe();
	}
}
