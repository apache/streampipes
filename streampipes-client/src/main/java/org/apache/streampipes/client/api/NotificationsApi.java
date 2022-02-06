package org.apache.streampipes.client.api;

import org.apache.streampipes.client.model.StreamPipesClientConfig;
import org.apache.streampipes.client.util.StreamPipesApiPath;
import org.apache.streampipes.model.Notification;

public class NotificationsApi extends AbstractTypedClientApi<Notification> {

  public NotificationsApi(StreamPipesClientConfig clientConfig) {
    super(clientConfig, Notification.class);
  }

  @Override
  protected StreamPipesApiPath getBaseResourcePath() {
    return StreamPipesApiPath.fromBaseApiPath()
            .addToPath("notifications");
  }

  public void add(Notification notification) {
    post(getBaseResourcePath(), notification);
  }
}
