package de.fzi.cep.sepa.actions.messaging.jms;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;

public class ActiveMQPublisher extends ActiveMQConnectionProvider implements AutoCloseable {

	 private final Session session;
	 private final MessageProducer producer;

    public ActiveMQPublisher(String url, String topicName) throws JMSException {
        session = startJmsConnection(url).createSession(false, Session.AUTO_ACKNOWLEDGE);
        producer = session.createProducer(session.createTopic(topicName));
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
}
