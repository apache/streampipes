package org.streampipes.messaging.jms;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.messaging.EventProducer;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.streampipes.model.grounding.JmsTransportProtocol;

import javax.jms.*;


public class ActiveMQPublisher implements EventProducer<JmsTransportProtocol> {

	private Connection connection;
	private Session session;
	private MessageProducer producer;

	private Boolean connected;

	public ActiveMQPublisher() {

	}

	// TODO backwards compatibility, remove later
	public ActiveMQPublisher(String url, String topic) {
		JmsTransportProtocol protocol = new JmsTransportProtocol();
		protocol.setBrokerHostname(url.substring(0, url.lastIndexOf(":")));
		protocol.setPort(Integer.parseInt(url.substring(url.lastIndexOf(":")+1, url.length())));
		protocol.setTopicName(topic);
		try {
			connect(protocol);
		} catch (SpRuntimeException e) {
			e.printStackTrace();
		}
	}

	public void sendText(String message) throws JMSException {
		publish(message.getBytes());
	}

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
			this.connected = true;
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
			this.connected = false;
			//logger.info("ActiveMQ connection closed successfully.");
		} catch (JMSException e) {
			//logger.warn("Could not close ActiveMQ connection.");
			throw new SpRuntimeException("could not disconnect from activemq broker");
		}
	}

	@Override
	public Boolean isConnected() {
		return connected;
	}

}