package de.fzi.cep.sepa.actions.dashboard;

import de.fzi.cep.sepa.commons.config.ClientConfiguration;
import de.fzi.cep.sepa.commons.messaging.IMessageListener;
import de.fzi.cep.sepa.commons.messaging.activemq.ActiveMQPublisher;

import javax.jms.JMSException;

public class Dashboard  implements IMessageListener<byte[]> {
    ActiveMQPublisher publisher;

    public Dashboard(String topic) {
        try {
            this.publisher = new ActiveMQPublisher(ClientConfiguration.INSTANCE.getJmsUrl(), topic);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEvent(byte[] payload) {
        try {
            publisher.sendBinary(payload);
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
