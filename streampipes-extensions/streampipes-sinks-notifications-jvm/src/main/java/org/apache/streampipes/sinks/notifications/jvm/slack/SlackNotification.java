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
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.wrapper.context.EventSinkRuntimeContext;
import org.apache.streampipes.wrapper.runtime.EventSink;

import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.SlackUser;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;

import java.io.IOException;


public class SlackNotification implements EventSink<SlackNotificationParameters> {

  private static final String HASHTAG = "#";

  private SlackNotificationParameters params;
  private SlackSession session;
  private Boolean sendToUser;

  @Override
  public void onInvocation(SlackNotificationParameters parameters, EventSinkRuntimeContext runtimeContext)
      throws SpRuntimeException {
    this.params = parameters;
    // Initialize slack session
    this.session = SlackSessionFactory.createWebSocketSlackSession(params.getAuthToken());
    try {
      this.session.connect();
    } catch (IOException e) {
      e.printStackTrace();
      throw new SpRuntimeException("Could not connect to Slack");
    }

    this.sendToUser = params.getChannelType().equals("User");

    if (this.sendToUser) {
      SlackUser user = session.findUserByUserName(params.getUserChannel());
      if (user == null) {
        throw new SpRuntimeException("The user: '" + params.getUserChannel() + "' does not exists");
      }
    } else {
      SlackChannel channel = session.findChannelByName(params.getUserChannel());
      if (channel == null || channel.getId() == null) {
        throw new SpRuntimeException("The channel: '" + params.getUserChannel() + "' does not "
            + "exists or "
            + "the bot has no rights to access it");
      }
    }
  }

  @Override
  public void onEvent(Event event) {
    String message = replacePlaceholders(event, params.getMessage());
    if (this.sendToUser) {
      this.session.sendMessageToUser(params.getUserChannel(),
          message, null);
    } else {
      SlackChannel channel = this.session.findChannelByName(params.getUserChannel());
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
