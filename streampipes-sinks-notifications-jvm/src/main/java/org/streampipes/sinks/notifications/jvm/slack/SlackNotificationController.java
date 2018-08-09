/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.sinks.notifications.jvm.slack;

import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.SlackUser;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;
import org.streampipes.commons.Utils;
import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.container.declarer.SemanticEventConsumerDeclarer;
import org.streampipes.messaging.kafka.SpKafkaConsumer;
import org.streampipes.model.DataSinkType;
import org.streampipes.model.Response;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataSinkDescription;
import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.model.grounding.EventGrounding;
import org.streampipes.model.grounding.KafkaTransportProtocol;
import org.streampipes.model.grounding.TransportFormat;
import org.streampipes.model.staticproperty.*;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.sdk.stream.SchemaBuilder;
import org.streampipes.sdk.stream.StreamBuilder;
import org.streampipes.sinks.notifications.jvm.config.ConfigKeys;
import org.streampipes.sinks.notifications.jvm.config.SinksNotificationsJvmConfig;
import org.streampipes.vocabulary.MessageFormat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//

public class SlackNotificationController implements SemanticEventConsumerDeclarer {

  SpKafkaConsumer kafkaConsumerGroup;
  SlackNotification slackNotification;

  @Override
  public Response invokeRuntime(DataSinkInvocation invocationGraph) {
//        String authToken = ((FreeTextStaticProperty) (SepaUtils.getStaticPropertyByInternalName(invocationGraph, "auth_token"))).getValue();
    String authToken = SinksNotificationsJvmConfig.INSTANCE.getSlackToken();

    if (authToken != ConfigKeys.SLACK_NOT_INITIALIZED) {
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
      boolean sendToUser = aggregateOperation.equals("User") ? true : false;

      if (sendToUser) {
        SlackUser user = session.findUserByUserName(userChannel);
        if (user == null) {
          return new Response(invocationGraph.getElementId(), false, "The user: '" + userChannel + "' does not exists");
        }
      } else {
        SlackChannel channel = session.findChannelByName(userChannel);
        if (channel == null || channel.getId() == null) {
          return new Response(invocationGraph.getElementId(), false, "The channel: '" + userChannel + "' does not exists or the bot has no rights to access it");
        }
      }


      List<String> properties = SepaUtils.getMultipleMappingPropertyNames(invocationGraph, "message", true);

      SlackNotificationParameters params = new SlackNotificationParameters(authToken, sendToUser, userChannel, properties, session);

      slackNotification = new SlackNotification(params);

      String consumerTopic = invocationGraph.getInputStreams().get(0).getEventGrounding().getTransportProtocol().getTopicDefinition().getActualTopicName();

      kafkaConsumerGroup = new SpKafkaConsumer(SinksNotificationsJvmConfig.INSTANCE.getKafkaUrl(), consumerTopic,
              slackNotification);
      Thread thread = new Thread(kafkaConsumerGroup);
      thread.start();


      return new Response(invocationGraph.getElementId(), true);
    } else {
      return new Response(invocationGraph.getElementId(), false, "There is no authentication slack token defined in the configuration");
    }
  }

  @Override
  public Response detachRuntime(String pipelineId) {

    try {
      kafkaConsumerGroup.disconnect();
      slackNotification.getParams().getSession().disconnect();
    } catch (IOException e) {
      e.printStackTrace();
      return new Response(pipelineId, false, e.toString());
    } catch (SpRuntimeException e) {
      return new Response(pipelineId, false, e.toString());
    }
    return new Response(pipelineId, true);
  }

  @Override
  public DataSinkDescription declareModel() {
    SpDataStream stream = StreamBuilder.createStream("", "", "").schema(SchemaBuilder.create().build()).build();
    DataSinkDescription desc = new DataSinkDescription("slack_sink", "Slack Notification", "Slack bot to send notifications directly into your slack");
    desc.setCategory(Arrays.asList(DataSinkType.ACTUATOR.name()));
    desc.setIconUrl(SinksNotificationsJvmConfig.iconBaseUrl + "/slack_icon.png");
    stream.setUri(SinksNotificationsJvmConfig.serverUrl + "/" + Utils.getRandomString());
    desc.addEventStream(stream);

    List<StaticProperty> staticProperties = new ArrayList<>();

//        staticProperties.add(new FreeTextStaticProperty("auth_token", "Slack Bot auth-token", "Enter the token of your slack bot"));
    staticProperties.add(new FreeTextStaticProperty("contact", "Sent to", "Enter the username or channel you want to notify"));

    OneOfStaticProperty userChannel = new OneOfStaticProperty("user_channel", "User or Channel", "Decide wether you want to sent a notification to a user or to a channel");
    userChannel.addOption(new Option("User"));
    userChannel.addOption(new Option("Channel"));
    staticProperties.add(userChannel);

    staticProperties.add(new MappingPropertyNary("message", "Select for message", "Select the properties for the notification"));

    desc.setStaticProperties(staticProperties);

    EventGrounding grounding = new EventGrounding();

    grounding.setTransportProtocol(new KafkaTransportProtocol(SinksNotificationsJvmConfig.INSTANCE.getKafkaHost(),
            SinksNotificationsJvmConfig.INSTANCE.getKafkaPort(), "", SinksNotificationsJvmConfig.INSTANCE.getZookeeperHost(),
            SinksNotificationsJvmConfig.INSTANCE.getZookeeperPort()));
    grounding.setTransportFormats(Arrays.asList(new TransportFormat(MessageFormat.Json)));
    desc.setSupportedGrounding(grounding);
    return desc;
  }


}
