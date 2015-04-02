package de.fzi.cep.sepa.runtime.param;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.component.jms.JmsConfiguration;

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
			PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory(factory);

	        JmsConfiguration config = new JmsConfiguration(pooledConnectionFactory);
	        //config.setConcurrentConsumers(1);
	        //config.setMaxConcurrentConsumers(1);
	        //config.setAsyncConsumer(true);
	        
	        ActiveMQComponent activeMQComponent = ActiveMQComponent.activeMQComponent();
	        activeMQComponent.setConfiguration(config);

	        
	        context.addComponent(brokerAlias, activeMQComponent);
			
			//context.addComponent(brokerAlias, JmsComponent.jmsComponentAutoAcknowledge(factory));
		}

		@Override
		public void removeFrom(CamelContext context) {
			context.removeComponent(brokerAlias);
		}
	}

    //TODO
    public static class Kafka implements CamelConfig {
        //kafka:server:port[?options]

        public Kafka() {

        }

        @Override
        public void applyTo(CamelContext context) {

        }

        @Override
        public void removeFrom(CamelContext context) {

        }
    }
}
