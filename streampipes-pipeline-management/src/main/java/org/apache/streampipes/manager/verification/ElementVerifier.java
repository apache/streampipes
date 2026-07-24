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

package org.apache.streampipes.manager.verification;

import org.apache.streampipes.commons.exceptions.NoServiceEndpointsAvailableException;
import org.apache.streampipes.manager.assets.AssetManager;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.client.user.Permission;
import org.apache.streampipes.model.client.user.PermissionBuilder;
import org.apache.streampipes.model.message.ErrorMessage;
import org.apache.streampipes.model.message.Message;
import org.apache.streampipes.model.message.Notification;
import org.apache.streampipes.model.message.NotificationType;
import org.apache.streampipes.model.message.SuccessMessage;
import org.apache.streampipes.resource.management.PermissionResourceManager;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.apache.streampipes.storage.api.pipeline.IPipelineElementDescriptionStorage;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class ElementVerifier<T extends NamedStreamPipesEntity> {

  private static final Logger LOG = LoggerFactory.getLogger(ElementVerifier.class);

  private final String graphData;
  private final Class<T> elementClass;
  private final boolean shouldTransform;

  protected T elementDescription;

  protected final IPipelineElementDescriptionStorage storageApi;
  protected final AssetManager assetManager;
  private final PermissionResourceManager permissionResourceManager;

  public ElementVerifier(
      String graphData,
      Class<T> elementClass,
      IPipelineElementDescriptionStorage storageApi,
      SpResourceManager resourceManager
  ) {
    this.elementClass = elementClass;
    this.graphData = graphData;
    this.storageApi = storageApi;
    this.shouldTransform = true;
    this.permissionResourceManager = resourceManager.managePermissions();
    this.assetManager = new AssetManager(resourceManager.getCoreConfigurationStorage());
  }

  public ElementVerifier(T elementDescription,
                         IPipelineElementDescriptionStorage storageApi,
                         SpResourceManager resourceManager) {
    this.elementDescription = elementDescription;
    this.storageApi = storageApi;
    this.graphData = null;
    this.elementClass = null;
    this.shouldTransform = false;
    this.permissionResourceManager = resourceManager.managePermissions();
    this.assetManager = new AssetManager(resourceManager.getCoreConfigurationStorage());

  }

  protected abstract StorageState store();

  protected abstract void update();

  public Message verifyAndAdd(String principalSid, boolean publicElement) {
    var transformError = transformEntity();
    if (transformError != null) {
      return transformError;
    }

    StorageState state = store();
    if (state == StorageState.STORED) {
      createAndStorePermission(principalSid, publicElement);
      try {
        storeAssets();
      } catch (IOException | NoServiceEndpointsAvailableException e) {
        LOG.error("Could not store assets for app id '{}'", elementDescription.getAppId(), e);
      }
      return successMessage();
    } else {
      return addedToUserSuccessMessage();
    }

  }

  public Message verifyAndUpdate() {
    var transformError = transformEntity();
    if (transformError != null) {
      return transformError;
    }

    update();
    try {
      updateAssets();
    } catch (IOException | NoServiceEndpointsAvailableException e) {
      LOG.error("Could not update assets for app id '{}'", elementDescription.getAppId(), e);
    }
    return successMessage();

  }

  protected abstract void storeAssets() throws IOException, NoServiceEndpointsAvailableException;

  protected void updateAssets() throws IOException, NoServiceEndpointsAvailableException {
    if (elementDescription.isIncludesAssets()) {
      assetManager.deleteAsset(elementDescription.getAppId());
      storeAssets();
    }
  }

  private Message successMessage() {
    List<Notification> notifications = new ArrayList<>();
    notifications.add(NotificationType.STORAGE_SUCCESS.uiNotification());
    return new SuccessMessage(elementDescription.getName(), notifications);
  }

  private Message addedToUserSuccessMessage() {
    List<Notification> notifications = new ArrayList<>();
    notifications.add(new Notification("Already stored", "Element description already stored, added element to user"));
    return new SuccessMessage(elementDescription.getName(), notifications);
  }

  private Message transformEntity() {
    if (!shouldTransform) {
      return null;
    }

    try {
      this.elementDescription = transform();
      return null;
    } catch (IOException e) {
      return new ErrorMessage(NotificationType.UNKNOWN_ERROR.uiNotification());
    }
  }

  protected T transform() throws JsonProcessingException {
    return JacksonSerializer.getObjectMapper()
                            .readValue(graphData, elementClass);
  }

  private void createAndStorePermission(
      String principalSid,
      boolean publicElement
  ) {
    Permission permission = makePermission(
        this.elementDescription.getElementId(),
        this.elementDescription.getClass(),
        principalSid, publicElement
    );

    storeNewObjectPermission(permission);
  }

  protected Permission makePermission(
      String objectInstanceId,
      Class<?> objectInstanceClass,
      String principalSid,
      boolean publicElement
  ) {
    return PermissionBuilder
      .create(objectInstanceId, objectInstanceClass, principalSid)
      .publicElement(publicElement)
      .build();
  }

  protected void storeNewObjectPermission(Permission permission) {
    permissionResourceManager.create(permission);
  }
}
