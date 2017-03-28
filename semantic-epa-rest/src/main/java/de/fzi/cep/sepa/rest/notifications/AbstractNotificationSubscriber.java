package de.fzi.cep.sepa.rest.notifications;

import de.fzi.cep.sepa.commons.config.Configuration;
import de.fzi.cep.sepa.messaging.EventListener;
import de.fzi.cep.sepa.messaging.kafka.StreamPipesKafkaConsumer;
import de.fzi.cep.sepa.model.client.messages.ProaSenseNotificationMessage;
import de.fzi.cep.sepa.storage.controller.StorageManager;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.protocol.TBinaryProtocol;

import java.text.SimpleDateFormat;

/**
 * Created by riemer on 16.10.2016.
 */
public abstract class AbstractNotificationSubscriber implements EventListener<byte[]>, Runnable {

    protected String topic;
    protected TDeserializer deserializer;

    public AbstractNotificationSubscriber(String topic) {
        this.topic = topic;
        this.deserializer = new TDeserializer(new TBinaryProtocol.Factory());
    }

    public void subscribe() {
        StreamPipesKafkaConsumer kafkaConsumerGroup = new StreamPipesKafkaConsumer(Configuration.getInstance().getBrokerConfig().getKafkaUrl(), topic,
                this);
        Thread thread = new Thread(kafkaConsumerGroup);
        thread.start();
    }

    @Override
    public void run() {
        subscribe();
    }

    protected void storeNotification(ProaSenseNotificationMessage message) {
        StorageManager
                .INSTANCE
                .getNotificationStorageApi()
                .addNotification(message);
    }

    protected String parseDate(long timestamp) {
        return new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(timestamp);
    }

}
