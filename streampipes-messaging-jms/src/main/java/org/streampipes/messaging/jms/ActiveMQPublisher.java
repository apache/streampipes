package org.streampipes.messaging.jms;

import org.streampipes.messaging.EventProducer;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;


public class ActiveMQPublisher implements EventProducer {
	private Connection connection;
	private Session session;
	private MessageProducer producer;
	
	public ActiveMQPublisher(String url, String topic) throws JMSException {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);

		// TODO fix this 
		// it works but we need a better solution
		// we should retry to connect when the service is not available immediately 
		boolean co = false;
		do {
			try {
				Thread.sleep(2000);
				this.connection = connectionFactory.createConnection();
				co = true;
			} catch (JMSException e) {
				e.printStackTrace();
				System.out.println("Trying to connect");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while (!co);

		this.session = connection
				.createSession(false, Session.AUTO_ACKNOWLEDGE);
		this.producer = session.createProducer(session.createTopic(topic));
		this.producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
		this.connection.start();

	}

	public void sendText(String text) throws JMSException {
		TextMessage message = session.createTextMessage(text);
		producer.send(message);
	}


	@Override
	public void openProducer() {

	}

	@Override
	public void publish(byte[] event)  {
		BytesMessage message = null;
		try {
			message = session.createBytesMessage();
			message.writeBytes(event);
			producer.send(message);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void publish(String message) {
		try {
			sendText(message);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void closeProducer() {
		try {
			producer.close();
			session.close();
			connection.close();
			//logger.info("ActiveMQ connection closed successfully.");
		} catch (JMSException e) {
			//logger.warn("Could not close ActiveMQ connection.");
		}
	}

}