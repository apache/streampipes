package de.fzi.cep.sepa.webapp.notifications;

import java.util.Date;

import javax.jms.JMSException;

import com.google.gson.Gson;

import de.fzi.cep.sepa.commons.config.Configuration;
import de.fzi.cep.sepa.commons.messaging.IMessageListener;
import de.fzi.cep.sepa.commons.messaging.activemq.ActiveMQPublisher;
import de.fzi.cep.sepa.commons.messaging.kafka.KafkaConsumerGroup;
import de.fzi.cep.sepa.messages.ProaSenseNotificationMessage;

public class NotificationSubscriber implements IMessageListener {

	private String kafkaTopic;
	private String websocketTopic;
	private ActiveMQPublisher publisher;
	
	public NotificationSubscriber()
	{
		this.kafkaTopic = "test";
		this.websocketTopic = "de.fzi.proasense.recommendation.websocket";
		try {
			this.publisher = new ActiveMQPublisher(Configuration.getInstance().getJmsAddress(), websocketTopic);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	
	public void startListening()
	{
		KafkaConsumerGroup kafkaConsumerGroup = new KafkaConsumerGroup(Configuration.getInstance().getBrokerConfig().getZookeeperUrl(), kafkaTopic,
				new String[] {kafkaTopic}, this);
		kafkaConsumerGroup.run(1);
	}


	@Override
	public void onEvent(String json) {
		try {
			String output = new Gson().toJson(new ProaSenseNotificationMessage("Test", new Date().getTime(), "Decription", "test-user"));
			publisher.sendText(output);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
