package de.fzi.cep.sepa.actions.samples.dashboard;

import de.fzi.cep.sepa.commons.config.ClientConfiguration;
import de.fzi.cep.sepa.messaging.EventListener;
import de.fzi.cep.sepa.messaging.jms.ActiveMQPublisher;

import javax.jms.JMSException;

public class Dashboard  implements EventListener<byte[]> {
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
        publisher.publish(payload);

    }
}
