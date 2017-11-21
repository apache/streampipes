package org.streampipes.rest.notifications;

import org.streampipes.model.Notification;

/**
 * Created by riemer on 16.10.2016.
 */
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
