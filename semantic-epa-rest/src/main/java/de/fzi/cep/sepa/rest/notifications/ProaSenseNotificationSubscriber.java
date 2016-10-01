package de.fzi.cep.sepa.rest.notifications;

import de.fzi.cep.sepa.commons.config.Configuration;
import de.fzi.cep.sepa.messaging.EventListener;
import de.fzi.cep.sepa.messaging.kafka.StreamPipesKafkaConsumer;
import de.fzi.cep.sepa.model.client.messages.ProaSenseNotificationMessage;
import de.fzi.cep.sepa.storage.controller.StorageManager;
import eu.proasense.internal.RecommendationEvent;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;

import java.text.SimpleDateFormat;

public class ProaSenseNotificationSubscriber implements EventListener<byte[]>, Runnable {

	
	private TDeserializer deserializer;
	private String topic;
	
	public ProaSenseNotificationSubscriber(String topic) {
		this.deserializer = new TDeserializer(new TBinaryProtocol.Factory());
		this.topic = topic;
	}
	
	public void subscribe()
	{
		StreamPipesKafkaConsumer kafkaConsumerGroup = new StreamPipesKafkaConsumer(Configuration.getInstance().getBrokerConfig().getKafkaUrl(), topic,
				this);
		Thread thread = new Thread(kafkaConsumerGroup);
		thread.start();
	}

	@Override
	public void onEvent(byte[] json) {
		RecommendationEvent event = new RecommendationEvent();
		String recommendedDate = "";
		try {
			deserializer.deserialize(event,  json);
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
