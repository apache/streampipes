package de.fzi.cep.sepa.rest.notifications;

import de.fzi.cep.sepa.model.client.messages.ProaSenseNotificationMessage;
import eu.proasense.internal.RecommendationEvent;
import org.apache.thrift.TException;

public class ProaSenseNotificationSubscriber extends AbstractNotificationSubscriber {


    public ProaSenseNotificationSubscriber(String topic) {
        super(topic);
    }

    @Override
    public void onEvent(byte[] thriftMessage) {
        RecommendationEvent event = new RecommendationEvent();
        String recommendedDate = "";
        try {
            deserializer.deserialize(event, thriftMessage);
            if (event.getEventProperties().containsKey("action_timestamp")) {
                recommendedDate += " at time "
                        + parseDate(Long.parseLong(event.getEventProperties().get("action_timestamp").getValue()))
                        +"<br/>"
                        + "(Recommendation ID: " + event.getRecommendationId()
                        + ", Time: " + parseDate(event.getTimestamp())
                        + ", "
                        + ")";
            }

            storeNotification(new ProaSenseNotificationMessage(event.getEventName(),
                    event.getTimestamp(),
                    event.getAction() + recommendedDate,
                    event.getActor()));

        } catch (TException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    }
