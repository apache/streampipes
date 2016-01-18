package de.fzi.cep.sepa.commons.messaging.activemq;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

import de.fzi.cep.sepa.commons.messaging.IMessagePublisher;


public class ActiveMQPublisher implements IMessagePublisher<String> {
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
	
	public void sendBinary(byte[] payload) throws JMSException {
		BytesMessage message = session.createBytesMessage();
		message.writeBytes(payload);
		producer.send(message);
	}

	public void close() throws JMSException {
		try {
			producer.close();
			session.close();
			connection.close();
			//logger.info("ActiveMQ connection closed successfully.");
		} catch (JMSException e) {
			//logger.warn("Could not close ActiveMQ connection.");
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
}