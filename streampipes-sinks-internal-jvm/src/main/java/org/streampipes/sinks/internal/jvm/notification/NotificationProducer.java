/*
 * Copyright 2017 FZI Forschungszentrum Informatik
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
 */

package org.streampipes.sinks.internal.jvm.notification;


import com.google.gson.Gson;
import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.messaging.jms.ActiveMQPublisher;
import org.streampipes.model.Notification;
import org.streampipes.sinks.internal.jvm.config.SinksInternalJvmConfig;
import org.streampipes.wrapper.runtime.EventSink;

import java.util.Date;
import java.util.Map;

public class NotificationProducer extends EventSink<NotificationParameters> {

  private String title;
  private String content;

  private ActiveMQPublisher publisher;
  private Gson gson;


  public NotificationProducer(NotificationParameters params) {
    super(params);

  }

  @Override
  public void bind(NotificationParameters parameters) throws SpRuntimeException {
    this.publisher = new ActiveMQPublisher(SinksInternalJvmConfig.INSTANCE.getJmsHost() + ":" + SinksInternalJvmConfig.INSTANCE.getJmsPort(),
            "org.streampipes.notifications");
    this.gson = new Gson();
    this.title = parameters.getTitle();
    this.content = parameters.getContent();
  }

  @Override
  public void onEvent(Map<String, Object> event, String sourceInfo) {
    Notification notification = new Notification();
    notification.setTitle(title);
    notification.setMessage(content);
    notification.setCreatedAt(new Date());

    // TODO add targeted user to notification object

    publisher.publish(gson.toJson(notification).getBytes());
  }

  @Override
  public void discard() throws SpRuntimeException {
    this.publisher.disconnect();
  }
}
