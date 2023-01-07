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
import org.apache.streampipes.commons.exceptions.SepaParseException;
import org.apache.streampipes.manager.verification.messages.VerificationError;
import org.apache.streampipes.manager.verification.messages.VerificationResult;
import org.apache.streampipes.manager.verification.structure.GeneralVerifier;
import org.apache.streampipes.manager.verification.structure.Verifier;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.client.user.Permission;
import org.apache.streampipes.model.client.user.PermissionBuilder;
import org.apache.streampipes.model.message.ErrorMessage;
import org.apache.streampipes.model.message.Message;
import org.apache.streampipes.model.message.Notification;
import org.apache.streampipes.model.message.NotificationType;
import org.apache.streampipes.model.message.SuccessMessage;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.apache.streampipes.storage.api.IPipelineElementDescriptionStorageCache;
import org.apache.streampipes.storage.management.StorageManager;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public abstract class ElementVerifier<T extends NamedStreamPipesEntity> {

  protected static final Logger LOGGER = Logger.getAnonymousLogger();

  private String graphData;
  private Class<T> elementClass;
  private boolean shouldTransform;

  protected T elementDescription;

  protected List<VerificationResult> validationResults;
  protected List<Verifier> validators;

  protected IPipelineElementDescriptionStorageCache storageApi =
      StorageManager.INSTANCE.getPipelineElementStorage();

  public ElementVerifier(String graphData, Class<T> elementClass) {
    this.elementClass = elementClass;
    this.graphData = graphData;
    this.shouldTransform = true;
    this.validators = new ArrayList<>();
    this.validationResults = new ArrayList<>();
  }

  public ElementVerifier(T elementDescription) {
    this.elementDescription = elementDescription;
    this.shouldTransform = false;
    this.validators = new ArrayList<>();
    this.validationResults = new ArrayList<>();
  }

  protected void collectValidators() {
    validators.add(new GeneralVerifier<>(elementDescription));
  }

  protected abstract StorageState store();

  protected abstract void update();

  protected void verify() {
    collectValidators();
    validators.forEach(validator -> validationResults.addAll(validator.validate()));
  }

  public Message verifyAndAdd(String principalSid, boolean publicElement) throws SepaParseException {
    if (shouldTransform) {
      try {
        this.elementDescription = transform();
      } catch (IOException e) {
        return new ErrorMessage(NotificationType.UNKNOWN_ERROR.uiNotification());
      }
    }
    verify();
    if (isVerifiedSuccessfully()) {
      StorageState state = store();
      if (state == StorageState.STORED) {
        createAndStorePermission(principalSid, publicElement);
        try {
          storeAssets();
        } catch (IOException | NoServiceEndpointsAvailableException e) {
          e.printStackTrace();
        }
        return successMessage();
      } else if (state == StorageState.ALREADY_IN_SESAME) {
        return addedToUserSuccessMessage();
      } else {
        return skippedSuccessMessage();
      }
    } else {
      return errorMessage();
    }

  }

  public Message verifyAndUpdate() throws SepaParseException {
    try {
      this.elementDescription = transform();
    } catch (JsonProcessingException e) {
      return new ErrorMessage(NotificationType.UNKNOWN_ERROR.uiNotification());
    }
    verify();
    if (isVerifiedSuccessfully()) {
      update();
      try {
        updateAssets();
      } catch (IOException | NoServiceEndpointsAvailableException e) {
        e.printStackTrace();
      }
      return successMessage();
    } else {
      return errorMessage();
    }

  }

  protected abstract void storeAssets() throws IOException, NoServiceEndpointsAvailableException;

  protected abstract void updateAssets() throws IOException, NoServiceEndpointsAvailableException;

  private Message errorMessage() {
    return new ErrorMessage(elementDescription.getName(), collectNotifications());
  }

  private Message successMessage() {
    List<Notification> notifications = collectNotifications();
    notifications.add(NotificationType.STORAGE_SUCCESS.uiNotification());
    return new SuccessMessage(elementDescription.getName(), notifications);
  }

  private Message skippedSuccessMessage() {
    List<Notification> notifications = collectNotifications();
    notifications.add(new Notification("Already exists", "This element is already in your list of elements, skipped."));
    return new SuccessMessage(elementDescription.getName(), notifications);
  }

  private Message addedToUserSuccessMessage() {
    List<Notification> notifications = collectNotifications();
    notifications.add(new Notification("Already stored", "Element description already stored, added element to user"));
    return new SuccessMessage(elementDescription.getName(), notifications);
  }

  private List<Notification> collectNotifications() {
    List<Notification> notifications = new ArrayList<>();
    validationResults.forEach(vr -> notifications.add(vr.getNotification()));
    return notifications;
  }

  private boolean isVerifiedSuccessfully() {
    return validationResults.stream().noneMatch(validator -> (validator instanceof VerificationError));
  }

  protected T transform() throws JsonProcessingException {
    return JacksonSerializer.getObjectMapper().readValue(graphData, elementClass);
  }

  private void createAndStorePermission(String principalSid,
                                        boolean publicElement) {
    Permission permission = makePermission(
        this.elementDescription.getElementId(),
        this.elementDescription.getClass(),
        principalSid, publicElement
    );

    storeNewObjectPermission(permission);
  }

  protected Permission makePermission(String objectInstanceId,
                                      Class<?> objectInstanceClass,
                                      String principalSid,
                                      boolean publicElement) {
    return PermissionBuilder
        .create(objectInstanceId, objectInstanceClass, principalSid)
        .publicElement(publicElement)
        .build();
  }

  protected void storeNewObjectPermission(Permission permission) {
    new SpResourceManager().managePermissions().create(permission);
  }
}
