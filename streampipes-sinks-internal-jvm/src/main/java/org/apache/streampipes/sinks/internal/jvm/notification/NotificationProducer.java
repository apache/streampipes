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

package org.apache.streampipes.sinks.internal.jvm.notification;


import com.google.gson.Gson;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.messaging.jms.ActiveMQPublisher;
import org.apache.streampipes.model.Notification;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.sinks.internal.jvm.config.SinksInternalJvmConfig;
import org.apache.streampipes.wrapper.context.EventSinkRuntimeContext;
import org.apache.streampipes.wrapper.runtime.EventSink;

import java.util.Date;
import java.util.UUID;

public class NotificationProducer implements EventSink<NotificationParameters> {

  private static final String HASHTAG = "#";

  private String title;
  private String content;
  private String correspondingPipelineId;
  private String correspondingUser;

  private ActiveMQPublisher publisher;
  private Gson gson;


  @Override
  public void onInvocation(NotificationParameters parameters, EventSinkRuntimeContext runtimeContext) throws
          SpRuntimeException {
    this.gson = new Gson();
    this.title = parameters.getTitle();
    this.content = parameters.getContent();
    this.correspondingPipelineId = parameters.getGraph().getCorrespondingPipeline();
    this.correspondingUser = parameters.getGraph().getCorrespondingUser();
    this.publisher = new ActiveMQPublisher(
            SinksInternalJvmConfig.INSTANCE.getJmsHost(),
            SinksInternalJvmConfig.INSTANCE.getJmsPort(),
            "org.apache.streampipes.notifications." + this.correspondingUser);
  }

  @Override
  public void onEvent(Event inputEvent) {
    Date currentDate = new Date();
    Notification notification = new Notification();
    notification.setId(UUID.randomUUID().toString());
    notification.setRead(false);
    notification.setTitle(title);
    notification.setMessage(replacePlaceholders(inputEvent, content));
    notification.setCreatedAt(currentDate);
    notification.setCreatedAtTimestamp(currentDate.getTime());
    notification.setCorrespondingPipelineId(correspondingPipelineId);
    notification.setTargetedAt(correspondingUser);

    // TODO add targeted user to notification object

    publisher.publish(gson.toJson(notification).getBytes());
  }

  @Override
  public void onDetach() throws SpRuntimeException {
    this.publisher.disconnect();
  }

  private String replacePlaceholders(Event event, String content) {
    for(String key: event.getRaw().keySet()) {
      content = content.replaceAll(HASHTAG + key + HASHTAG, event.getRaw().get(key).toString());
    }
    return content;
  }
}
