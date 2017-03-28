package de.fzi.cep.sepa.messaging.jms;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.JMSException;

public abstract class ActiveMQConnectionProvider {

    protected Connection startJmsConnection(String url) {
        try {
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
            connectionFactory.setAlwaysSyncSend(false);
            Connection connect = connectionFactory.createConnection();

            connect.start();
            return connect;
        } catch (JMSException e) {
            throw new AssertionError("Failed to establish the JMS-Connection!", e);
        }
    }
}
