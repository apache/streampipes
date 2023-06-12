/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.sinks.notifications.jvm.slack;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventSinkRuntimeContext;
import org.apache.streampipes.model.DataSinkType;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.sdk.builder.DataSinkBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.params.compat.SinkParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataSink;

import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.SlackUser;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;

import java.io.IOException;

public class SlackNotificationSink extends StreamPipesDataSink {

  private static final String CHANNEL_TYPE = "channel-type";
  private static final String RECEIVER = "receiver";
  private static final String CONTENT = "content";
  private static final String AUTH_TOKEN = "auth-token";

  private static final String HASHTAG = "#";

  private SlackSession session;
  private Boolean sendToUser;

  private String userChannel;
  private String originalMessage;

  @Override
  public DataSinkDescription declareModel() {

    return DataSinkBuilder.create("org.apache.streampipes.sinks.notifications.jvm.slack")
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .category(DataSinkType.NOTIFICATION)
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredProperty(EpRequirements.anyProperty())
            .build())
        .requiredTextParameter(Labels.withId(RECEIVER))
        .requiredTextParameter(Labels.withId(CONTENT), false, true)
        .requiredSingleValueSelection(Labels.withId(CHANNEL_TYPE),
            Options.from("User", "Channel"))
        .requiredSecret(Labels.withId(AUTH_TOKEN))
        .build();
  }

  @Override
  public void onInvocation(SinkParams parameters,
                           EventSinkRuntimeContext runtimeContext) throws SpRuntimeException {
    var extractor = parameters.extractor();

    userChannel = extractor.singleValueParameter(RECEIVER, String.class);
    originalMessage = extractor.singleValueParameter(CONTENT, String.class);

    String channelType = extractor.selectedSingleValue(CHANNEL_TYPE, String.class);
    String authToken = extractor.secretValue(AUTH_TOKEN);

    // Initialize slack session
    this.session = SlackSessionFactory.createWebSocketSlackSession(authToken);
    try {
      this.session.connect();
    } catch (IOException e) {
      e.printStackTrace();
      throw new SpRuntimeException("Could not connect to Slack");
    }

    this.sendToUser = channelType.equals("User");

    if (this.sendToUser) {
      SlackUser user = session.findUserByUserName(userChannel);
      if (user == null) {
        throw new SpRuntimeException("The user: '" + userChannel + "' does not exists");
      }
    } else {
      SlackChannel channel = session.findChannelByName(userChannel);
      if (channel == null || channel.getId() == null) {
        throw new SpRuntimeException("The channel: '" + userChannel + "' does not "
            + "exists or "
            + "the bot has no rights to access it");
      }
    }

  }

  @Override
  public void onEvent(Event event) throws SpRuntimeException {
    String message = replacePlaceholders(event, originalMessage);
    if (this.sendToUser) {
      this.session.sendMessageToUser(userChannel,
          message, null);
    } else {
      SlackChannel channel = this.session.findChannelByName(userChannel);
      this.session.sendMessage(channel, message);
    }
  }

  @Override
  public void onDetach() throws SpRuntimeException {
    try {
      this.session.disconnect();
    } catch (IOException e) {
      throw new SpRuntimeException("Could not disconnect");
    }
  }

  private String replacePlaceholders(Event event, String content) {
    for (String key : event.getRaw().keySet()) {
      content = content.replaceAll(HASHTAG + key + HASHTAG, event.getRaw().get(key).toString());
    }
    return content;
  }
}
