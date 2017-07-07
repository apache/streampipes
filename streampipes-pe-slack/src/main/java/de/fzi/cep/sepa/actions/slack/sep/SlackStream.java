package de.fzi.cep.sepa.actions.slack.sep;

import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.SlackUser;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;
import com.ullink.slack.simpleslackapi.listeners.SlackMessagePostedListener;
import de.fzi.cep.sepa.client.declarer.EventStreamDeclarer;
import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.commons.config.ClientConfiguration;
import de.fzi.cep.sepa.messaging.kafka.StreamPipesKafkaProducer;
import de.fzi.cep.sepa.model.impl.*;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.model.vocabulary.MessageFormat;
import de.fzi.cep.sepa.model.vocabulary.SO;
import de.fzi.cep.sepa.model.vocabulary.XSD;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SlackStream implements EventStreamDeclarer {
    private static String  topic = "slack.stream";

    private String name;
    private String description;
    private SlackSession session;

    public SlackStream(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public EventStream declareModel(SepDescription sep) {
        EventStream stream = new EventStream();

        stream.setName(name);
        stream.setDescription(description);
        stream.setUri(sep.getUri() + "/" + "slack");
//        stream.setUri("http://localhost:8080/slack/sep/slack" + "/" + name);


        EventGrounding grounding = new EventGrounding();
        grounding.setTransportProtocol(new KafkaTransportProtocol(ClientConfiguration.INSTANCE.getKafkaHost(), ClientConfiguration.INSTANCE.getKafkaPort(), topic, ClientConfiguration.INSTANCE.getZookeeperHost(), ClientConfiguration.INSTANCE.getZookeeperPort()));
        grounding.setTransportFormats(Arrays.asList(new TransportFormat(MessageFormat.Json)));
        stream.setEventGrounding(grounding);

        EventSchema schema = new EventSchema();
        List<EventProperty> eventProperties = new ArrayList<>();
        eventProperties.add(new EventPropertyPrimitive(XSD._string.toString(), "timestamp", "", Utils.createURI("http://schema.org/DateTime")));
        eventProperties.add(new EventPropertyPrimitive(XSD._string.toString(), "author", "", Utils.createURI(SO.Text)));
        eventProperties.add(new EventPropertyPrimitive(XSD._string.toString(), "message", "", Utils.createURI(SO.Text)));
        schema.setEventProperties(eventProperties);
        stream.setEventSchema(schema);

        return stream;
    }

    @Override
    public void executeStream() {
        SlackMessagePostedListener messagePostedListener = new SlackMessagePostedListener()
        {
            private StreamPipesKafkaProducer producer = new StreamPipesKafkaProducer(ClientConfiguration.INSTANCE.getKafkaUrl(), topic);

            @Override
            public void onEvent(SlackMessagePosted event, SlackSession session)
            {
                String botName = session.sessionPersona().getUserName();
                SlackChannel channelOnWhichMessageWasPosted = event.getChannel();
                String messageContent = event.getMessageContent();
                SlackUser messageSender = event.getSender();

                if (!messageSender.getUserName().equals(botName)) {
                    JSONObject object = new JSONObject();
                    object.put("timestamp", System.currentTimeMillis());
                    object.put("author", messageSender.getUserName());
                    object.put("message", messageContent);

                    producer.publish(object.toString().getBytes());
                }
            }
        };

        String token = ClientConfiguration.INSTANCE.getSlackToken();
        if (token != null) {
            session = SlackSessionFactory.createWebSocketSlackSession(token);
            try {
                session.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            session.addMessagePostedListener(messagePostedListener);
        } else {
            try {
                throw new Exception("Slack Token is not set in the client configuratons file");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean isExecutable() {
        return true;
    }
}
