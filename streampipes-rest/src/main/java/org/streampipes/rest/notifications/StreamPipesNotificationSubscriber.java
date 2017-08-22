package org.streampipes.rest.notifications;

import org.streampipes.model.client.messages.ProaSenseNotificationMessage;
import eu.proasense.internal.RecommendationEvent;
import org.apache.thrift.TException;

/**
 * Created by riemer on 16.10.2016.
 */
public class StreamPipesNotificationSubscriber extends AbstractNotificationSubscriber {

    public StreamPipesNotificationSubscriber(String topic) {
        super(topic);
    }

    @Override
    public void onEvent(byte[] thriftMessage) {
        RecommendationEvent event = new RecommendationEvent();
        try {
            deserializer.deserialize(event, thriftMessage);
            storeNotification(new ProaSenseNotificationMessage(event.getEventName(),
                    event.getTimestamp(),
                    event.getAction(),
                    event.getActor()));

        } catch (TException e) {
            e.printStackTrace();
        }
    }
}
