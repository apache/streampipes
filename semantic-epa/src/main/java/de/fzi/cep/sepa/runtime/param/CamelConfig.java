package de.fzi.cep.sepa.runtime.param;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.component.jms.JmsComponent;

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
			ConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
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
