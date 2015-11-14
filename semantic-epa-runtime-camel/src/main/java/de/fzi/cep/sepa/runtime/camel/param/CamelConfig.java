package de.fzi.cep.sepa.runtime.camel.param;

import javax.jms.DeliveryMode;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.component.jms.JmsConfiguration;
import org.apache.camel.component.kafka.KafkaComponent;

public interface CamelConfig { // every config that cannot be made with the simple endpoint uri string

	void applyTo(CamelContext context);

	void removeFrom(CamelContext context);

	public static class ActiveMQ implements CamelConfig { // also e.g. sqs, ftp, jpa ...

		private final String brokerAlias; // e.g. test-jms -> "test-jms:topic:test.destination"

		private final String brokerUrl;
		

		public ActiveMQ(String brokerAlias, String brokerUrl) {
			this.brokerAlias = brokerAlias;
			this.brokerUrl = brokerUrl;
		}

		@Override
		public void applyTo(CamelContext context) {
			ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
			//factory.setUseAsyncSend(true);
			//factory.setDispatchAsync(false);
			
			
			PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory(factory);
			pooledConnectionFactory.setMaxConnections(10000);
			//pooledConnectionFactory.set
	        JmsConfiguration config = new JmsConfiguration(pooledConnectionFactory);
	        //config.setConcurrentConsumers(1);
	        //config.setMaxConcurrentConsumers(1);
	        config.setAsyncConsumer(true);
	        
	        //config.setAcknowledgementModeName(JmsConfiguration.J);
	        
	        ActiveMQComponent activeMQComponent = ActiveMQComponent.activeMQComponent();
	        activeMQComponent.setConfiguration(config);
	        activeMQComponent.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
	        activeMQComponent.setAcknowledgementMode(Session.AUTO_ACKNOWLEDGE);
	        
	        //context.addComponent(brokerAlias, activeMQComponent);
			
			context.addComponent(brokerAlias, JmsComponent.jmsComponentAutoAcknowledge(factory));
		}

		@Override
		public void removeFrom(CamelContext context) {
			context.removeComponent(brokerAlias);
		}
	}

    //TODO
    public static class Kafka implements CamelConfig {
        //kafka:server:port[?options]

    	private final String brokerAlias;
    	
        public Kafka(String brokerAlias, String zookeeperHost, int zookeeperPort) {
        	this.brokerAlias = brokerAlias;
        }

        @Override
        public void applyTo(CamelContext context) {
             context.addComponent(brokerAlias, new KafkaComponent());
        }

        @Override
        public void removeFrom(CamelContext context) {
        	context.removeComponent(brokerAlias);
        }
    }
}
