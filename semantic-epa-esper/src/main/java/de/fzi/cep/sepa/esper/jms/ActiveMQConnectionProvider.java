package de.fzi.cep.sepa.esper.jms;

import javax.jms.Connection;
import javax.jms.JMSException;

import org.apache.activemq.ActiveMQConnectionFactory;

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
