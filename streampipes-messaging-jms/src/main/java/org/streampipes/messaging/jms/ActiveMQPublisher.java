package org.streampipes.messaging.jms;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.messaging.EventProducer;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.streampipes.model.impl.JmsTransportProtocol;

import javax.jms.*;


public class ActiveMQPublisher implements EventProducer<JmsTransportProtocol> {

	private Connection connection;
	private Session session;
	private MessageProducer producer;


	@Override
	public void connect(JmsTransportProtocol protocolSettings) throws SpRuntimeException {

		String url = protocolSettings.getBrokerHostname() +":" +protocolSettings.getPort();
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

		try {
			this.session = connection
              .createSession(false, Session.AUTO_ACKNOWLEDGE);
			this.producer = session.createProducer(session.createTopic(protocolSettings.getTopicName()));
			this.producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			this.connection.start();
		} catch (JMSException e) {
			throw new SpRuntimeException("could not connect to activemq broker");
		}

	}

	@Override
	public void publish(byte[] event)  {
		BytesMessage message;
		try {
			message = session.createBytesMessage();
			message.writeBytes(event);
			producer.send(message);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void disconnect() throws SpRuntimeException {
		try {
			producer.close();
			session.close();
			connection.close();
			//logger.info("ActiveMQ connection closed successfully.");
		} catch (JMSException e) {
			//logger.warn("Could not close ActiveMQ connection.");
			throw new SpRuntimeException("could not disconnect from activemq broker");
		}
	}

}