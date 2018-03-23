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

package org.streampipes.rest.notifications;

import org.streampipes.model.Notification;

public class StreamPipesNotificationSubscriber extends AbstractNotificationSubscriber {

  public StreamPipesNotificationSubscriber(String topic) {
    super(topic);
  }

  @Override
  public void onEvent(byte[] notificationMessage) {
    Notification notification = gson.fromJson(new String(notificationMessage), Notification.class);
    storeNotification(notification);

  }
}
