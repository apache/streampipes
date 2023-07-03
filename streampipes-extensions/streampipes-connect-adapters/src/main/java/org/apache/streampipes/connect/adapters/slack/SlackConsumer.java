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
package org.apache.streampipes.connect.adapters.slack;

import org.apache.streampipes.extensions.api.connect.IEventCollector;

import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.SlackUser;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;
import com.ullink.slack.simpleslackapi.listeners.SlackMessagePostedListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SlackConsumer implements Runnable {

  private final IEventCollector collector;
  private final String apiToken;
  private SlackSession session;

  public SlackConsumer(IEventCollector collector,
                       String slackApiToken) {
    this.collector = collector;
    this.apiToken = slackApiToken;
  }

  public void run() {
    SlackMessagePostedListener messagePostedListener = (event, session) -> {
      String botName = session.sessionPersona().getUserName();
      String channelOnWhichMessageWasPosted = event.getChannel().getName();
      String messageContent = event.getMessageContent();
      SlackUser messageSender = event.getSender();

      if (!messageSender.getUserName().equals(botName)) {
        Map<String, Object> outEvent = new HashMap<>();
        outEvent.put("timestamp", System.currentTimeMillis());
        outEvent.put("channel", channelOnWhichMessageWasPosted);
        outEvent.put("author", messageSender.getUserName());
        outEvent.put("message", messageContent);

        collector.collect(outEvent);
      }
    };

    this.session = SlackSessionFactory.createWebSocketSlackSession(apiToken);
    try {
      this.session.connect();
    } catch (IOException e) {
      e.printStackTrace();
    }
    this.session.addMessagePostedListener(messagePostedListener);
  }

  public void stop() {
    try {
      this.session.disconnect();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}


