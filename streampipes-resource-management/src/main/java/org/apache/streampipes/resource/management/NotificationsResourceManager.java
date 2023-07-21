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

package org.apache.streampipes.resource.management;

import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.storage.api.INotificationStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class NotificationsResourceManager extends AbstractResourceManager<INotificationStorage> {

  private static final String InternalNotificationsAppId = "org.apache.streampipes.sinks.internal.jvm.notification";
  private static final String InternalNotificationsTitleKey = "title";

  private static final Logger LOG = LoggerFactory.getLogger(NotificationsResourceManager.class);

  public NotificationsResourceManager() {
    super(StorageDispatcher.INSTANCE.getNoSqlStore().getNotificationStorageApi());
  }

  public void deleteNotificationsForPipeline(Pipeline pipeline) {
    try {
      List<String> notificationTypes = getAllNotificationTypesForPipeline(pipeline);
      notificationTypes.forEach(notificationType -> {
        var notifications = db.getAllNotifications(notificationType);
        notifications.forEach(notification -> db.deleteNotification(notification.getId()));
      });
    } catch (IllegalArgumentException e) {
      LOG.error("Could not find a notification type which is part of the pipeline.");
    }
  }

  private List<String> getAllNotificationTypesForPipeline(Pipeline pipeline) {
    return pipeline
        .getActions()
        .stream()
        .filter(sink -> sink.getAppId().equals(InternalNotificationsAppId))
        .map(sink -> sink.getCorrespondingPipeline() + "-" + extractNotificationTitle(sink.getStaticProperties()))
        .toList();
  }

  private String extractNotificationTitle(List<StaticProperty> staticProperties) {
    return staticProperties
        .stream()
        .filter(sp -> sp instanceof FreeTextStaticProperty)
        .filter(sp -> sp.getInternalName().equals(InternalNotificationsTitleKey))
        .map(sp -> ((FreeTextStaticProperty) sp).getValue())
        .findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }


}
