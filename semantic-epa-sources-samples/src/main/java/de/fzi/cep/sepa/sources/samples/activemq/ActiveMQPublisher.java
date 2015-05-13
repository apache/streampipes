package de.fzi.cep.sepa.sources.samples.activemq;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.jms.*;

import org.apache.activemq.ActiveMQConnectionFactory;


public class ActiveMQPublisher {
	private Connection connection;
	private Session session;
	private MessageProducer producer;

	//private static Logger logger = Logger.getLogger("CEP");

	public ActiveMQPublisher(String url, String subject) throws JMSException {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);

		this.connection = connectionFactory.createConnection();
		this.session = connection
				.createSession(false, Session.AUTO_ACKNOWLEDGE);
		this.producer = session.createProducer(session.createTopic(subject));
		this.producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
		this.connection.start();

	}

	public void sendText(String text) throws JMSException {
		//logger.info("Sending message to broker: " + text);
		TextMessage message = session.createTextMessage(text);
		producer.send(message);
	}
	
	public void sendBinary(byte[] payload) throws JMSException {
		BytesMessage message = session.createBytesMessage();
		message.writeBytes(payload);
		producer.send(message);
	}

	public void sendTextFile(File textFile) throws JMSException, IOException {
		FileInputStream stream = new FileInputStream(textFile);
		//sendText(Common.streamToString(stream));
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
}