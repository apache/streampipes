package de.fzi.cep.sepa.actions.slack.sec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;

import de.fzi.cep.sepa.actions.slack.config.SlackConfig;
import de.fzi.cep.sepa.client.declarer.SemanticEventConsumerDeclarer;
import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.commons.config.ClientConfiguration;
import de.fzi.cep.sepa.commons.messaging.kafka.KafkaConsumerGroup;
import de.fzi.cep.sepa.model.builder.SchemaBuilder;
import de.fzi.cep.sepa.model.builder.StreamBuilder;
import de.fzi.cep.sepa.model.impl.EcType;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.KafkaTransportProtocol;
import de.fzi.cep.sepa.model.impl.Response;
import de.fzi.cep.sepa.model.impl.TransportFormat;
import de.fzi.cep.sepa.model.impl.graph.SecDescription;
import de.fzi.cep.sepa.model.impl.graph.SecInvocation;
import de.fzi.cep.sepa.model.impl.staticproperty.FreeTextStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.MappingPropertyNary;
import de.fzi.cep.sepa.model.impl.staticproperty.OneOfStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.Option;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;
import de.fzi.cep.sepa.model.util.SepaUtils;
import de.fzi.cep.sepa.model.vocabulary.MessageFormat;

//

public class SlackNotificationController implements SemanticEventConsumerDeclarer {

    KafkaConsumerGroup kafkaConsumerGroup;
    SlackNotification slackNotification;

    @Override
    public Response invokeRuntime(SecInvocation invocationGraph) {
        String authToken = ((FreeTextStaticProperty) (SepaUtils.getStaticPropertyByInternalName(invocationGraph, "auth_token"))).getValue();

        // Initialize slack session
        SlackSession session = SlackSessionFactory.createWebSocketSlackSession(authToken);
        try {
            session.connect();
        } catch (IOException e) {
            e.printStackTrace();
            return new Response(invocationGraph.getElementId(), false, e.toString());
        }

        String userChannel = ((FreeTextStaticProperty) (SepaUtils.getStaticPropertyByInternalName(invocationGraph, "contact"))).getValue();

        String aggregateOperation = SepaUtils.getOneOfProperty(invocationGraph, "user_channel");
        boolean sendToUser = aggregateOperation == "User" ? true : false;


        List<String> properties = SepaUtils.getMultipleMappingPropertyNames(invocationGraph, "message", true);

        SlackNotificationParameters params = new SlackNotificationParameters(authToken, sendToUser, userChannel, properties, session);

        slackNotification = new SlackNotification(params);

        String consumerTopic = invocationGraph.getInputStreams().get(0).getEventGrounding().getTransportProtocol().getTopicName();

        kafkaConsumerGroup = new KafkaConsumerGroup(ClientConfiguration.INSTANCE.getZookeeperUrl(), consumerTopic,
                new String[] {consumerTopic}, slackNotification);
        kafkaConsumerGroup.run(1);


        return new Response(invocationGraph.getElementId(), true);
    }

    @Override
    public Response detachRuntime(String pipelineId) {

        kafkaConsumerGroup.shutdown();

        try {
            slackNotification.getParams().getSession().disconnect();
        } catch (IOException e) {
            e.printStackTrace();
            return new Response(pipelineId, false, e.toString());
        }
        return new Response(pipelineId, true);
    }

    @Override
    public SecDescription declareModel() {
        EventStream stream = StreamBuilder.createStream("", "","").schema(SchemaBuilder.create().build()).build();
        SecDescription desc = new SecDescription("slack_sink", "Slack Notification", "Slack bot to send notifications directly into your slack");
        desc.setEcTypes(Arrays.asList(EcType.ACTUATOR.name()));
        desc.setIconUrl(SlackConfig.iconBaseUrl + "/slack_icon.png");
        stream.setUri(SlackConfig.serverUrl +"/" + Utils.getRandomString());
        desc.addEventStream(stream);

        List<StaticProperty> staticProperties = new ArrayList<>();

        staticProperties.add(new FreeTextStaticProperty("auth_token", "Slack Bot auth-token", "Enter the token of your slack bot"));
        staticProperties.add(new FreeTextStaticProperty("contact", "Sent to", "Enter the username or channel you want to notify"));

        OneOfStaticProperty userChannel = new OneOfStaticProperty("user_channel", "User or Channel", "Decide wether you want to sent a notification to a user or to a channel");
        userChannel.addOption(new Option("User"));
        userChannel.addOption(new Option("Channel"));
        staticProperties.add(userChannel);

        staticProperties.add(new MappingPropertyNary("message", "Select for message", "Select the properties for the notification"));

        desc.setStaticProperties(staticProperties);

        EventGrounding grounding = new EventGrounding();

        grounding.setTransportProtocol(new KafkaTransportProtocol(ClientConfiguration.INSTANCE.getKafkaHost(), ClientConfiguration.INSTANCE.getKafkaPort(), "", ClientConfiguration.INSTANCE.getZookeeperHost(), ClientConfiguration.INSTANCE.getZookeeperPort()));
        grounding.setTransportFormats(Arrays.asList(new TransportFormat(MessageFormat.Json)));
        desc.setSupportedGrounding(grounding);
        return desc;
    }

    @Override
    public boolean isVisualizable() {
        return false;
    }

    @Override
    public String getHtml(SecInvocation graph) {
        return null;
    }

}
