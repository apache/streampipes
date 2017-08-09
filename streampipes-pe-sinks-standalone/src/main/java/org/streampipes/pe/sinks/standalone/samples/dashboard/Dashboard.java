package org.streampipes.pe.sinks.standalone.samples.dashboard;

import org.streampipes.messaging.EventListener;
import org.streampipes.messaging.jms.ActiveMQPublisher;
import org.streampipes.pe.sinks.standalone.config.ActionConfig;

import javax.jms.JMSException;

public class Dashboard  implements EventListener<byte[]> {
    ActiveMQPublisher publisher;

    public Dashboard(String topic) {
        try {
            this.publisher = new ActiveMQPublisher(ActionConfig.INSTANCE.getJmsUrl(), topic);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEvent(byte[] payload) {
        publisher.publish(payload);

    }
}
