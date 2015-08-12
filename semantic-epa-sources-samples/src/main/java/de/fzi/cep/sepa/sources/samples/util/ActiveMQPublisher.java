package de.fzi.cep.sepa.sources.samples.util;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

public class ActiveMQPublisher implements AutoCloseable {

	private final String brokerUrl = "tcp://localhost:61616";

	private final Session session;
	private final MessageProducer producer;

	public ActiveMQPublisher(String topicName) {
		try {
			Connection connection = startJmsConnection();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			producer = session.createProducer(session.createTopic(topicName));
		} catch (JMSException e) {
			throw new AssertionError(e);
		}
	}

	public void send(String json) {
		try {
			producer.send(session.createTextMessage(json));
		} catch (JMSException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void close() throws JMSException {
		producer.close();
		session.close();
	}

	private Connection startJmsConnection() {
		boolean connect = false;
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
		Connection connection = null;

		// TODO find a better solution
		do {
			try {
				connection = connectionFactory.createConnection();
				connection.start();
				connect = true;
			} catch (JMSException e) {
				System.out.println("Trying to connect to ActiveMQ");
			}

		} while (!connect);
		return connection;
	}
}