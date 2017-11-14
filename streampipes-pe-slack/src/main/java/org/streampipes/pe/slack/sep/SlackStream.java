package org.streampipes.pe.slack.sep;

import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.SlackUser;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;
import com.ullink.slack.simpleslackapi.listeners.SlackMessagePostedListener;
import org.streampipes.container.declarer.EventStreamDeclarer;
import org.streampipes.commons.Utils;
import org.streampipes.messaging.kafka.SpKafkaProducer;
import org.streampipes.model.grounding.EventGrounding;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.grounding.KafkaTransportProtocol;
import org.streampipes.model.grounding.TransportFormat;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventPropertyPrimitive;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.vocabulary.MessageFormat;
import org.streampipes.vocabulary.SO;
import org.streampipes.vocabulary.XSD;
import org.json.JSONObject;
import org.streampipes.pe.slack.config.SlackConfig;

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
    public SpDataStream declareModel(DataSourceDescription sep) {
        SpDataStream stream = new SpDataStream();

        stream.setName(name);
        stream.setDescription(description);
        stream.setUri(sep.getUri() + "/" + "slack");
//        stream.setUri("http://localhost:8080/slack/sep/slack" + "/" + name);


        EventGrounding grounding = new EventGrounding();
        grounding.setTransportProtocol(new KafkaTransportProtocol(SlackConfig.INSTANCE.getKafkaHost(),
                SlackConfig.INSTANCE.getKafkaPort(), topic, SlackConfig.INSTANCE.getZookeeperHost(),
                SlackConfig.INSTANCE.getZookeeperPort()));
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
            private SpKafkaProducer producer = new SpKafkaProducer(SlackConfig.INSTANCE.getKafkaUrl(),
                    topic);

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

        String token = SlackConfig.INSTANCE.getSlackToken();
        if (token != SlackConfig.SLACK_NOT_INITIALIZED) {
            session = SlackSessionFactory.createWebSocketSlackSession(token);
            try {
                session.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            session.addMessagePostedListener(messagePostedListener);
        } else {
            try {
                throw new Exception("Slack Token is not set in the configuratons");
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
