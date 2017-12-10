package org.streampipes.rest.notifications;

import com.google.gson.Gson;
import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.config.backend.BackendConfig;
import org.streampipes.messaging.InternalEventProcessor;
import org.streampipes.messaging.jms.ActiveMQConsumer;
import org.streampipes.model.Notification;
import org.streampipes.model.grounding.JmsTransportProtocol;
import org.streampipes.storage.controller.StorageManager;

import java.text.SimpleDateFormat;

/**
 * Created by riemer on 16.10.2016.
 */
public abstract class AbstractNotificationSubscriber implements InternalEventProcessor<byte[]>, Runnable {

    protected String topic;
    protected Gson gson;

    public AbstractNotificationSubscriber(String topic) {
        this.topic = topic;
        this.gson = new Gson();
    }

    public void subscribe() throws SpRuntimeException {
        ActiveMQConsumer consumer = new ActiveMQConsumer();
        consumer.connect(getConsumerSettings(), this);
    }

    private JmsTransportProtocol getConsumerSettings() {
        JmsTransportProtocol protocol = new JmsTransportProtocol();
        protocol.setPort(BackendConfig.INSTANCE.getJmsPort());
        protocol.setBrokerHostname("tcp://" +BackendConfig.INSTANCE.getJmsHost());
        protocol.setTopicName(topic);

        return protocol;
    }

    @Override
    public void run() {
        try {
            subscribe();
        } catch (SpRuntimeException e) {
            e.printStackTrace();
        }
    }

    protected void storeNotification(Notification message) {
        StorageManager
                .INSTANCE
                .getNotificationStorageApi()
                .addNotification(message);
    }

    protected String parseDate(long timestamp) {
        return new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(timestamp);
    }

}
