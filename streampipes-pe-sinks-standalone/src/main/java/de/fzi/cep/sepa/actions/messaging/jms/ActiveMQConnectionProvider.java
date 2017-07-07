package de.fzi.cep.sepa.actions.messaging.jms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import org.apache.activemq.ActiveMQConnectionFactory;

public abstract class ActiveMQConnectionProvider {

	 protected Connection startJmsConnection(String url) {
	        try {
	            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
	            Connection connect = connectionFactory.createConnection();
	            connect.start();
	            return connect;
	        } catch (JMSException e) {
	            throw new AssertionError("Failed to establish the JMS-Connection!", e);
	        }
	    }
}
