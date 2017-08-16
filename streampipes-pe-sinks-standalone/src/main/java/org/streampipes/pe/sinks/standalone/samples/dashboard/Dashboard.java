package org.streampipes.pe.sinks.standalone.samples.dashboard;

import org.streampipes.messaging.InternalEventProcessor;
import org.streampipes.messaging.jms.ActiveMQPublisher;
import org.streampipes.pe.sinks.standalone.config.ActionConfig;

public class Dashboard  implements InternalEventProcessor<byte[]> {
    ActiveMQPublisher publisher;

    public Dashboard(String topic) {
        this.publisher = new ActiveMQPublisher(ActionConfig.INSTANCE.getJmsUrl(), topic);
    }

    @Override
    public void onEvent(byte[] payload) {
        publisher.publish(payload);

    }
}
