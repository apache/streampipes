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
package org.apache.streampipes.rest.core.base.impl;

import org.apache.streampipes.model.message.ErrorMessage;
import org.apache.streampipes.model.message.Message;
import org.apache.streampipes.model.message.Notification;
import org.apache.streampipes.model.message.SuccessMessage;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.rest.shared.impl.AbstractSharedRestInterface;
import org.apache.streampipes.storage.api.INoSqlStorage;
import org.apache.streampipes.storage.api.INotificationStorage;
import org.apache.streampipes.storage.api.IPipelineElementDescriptionStorage;
import org.apache.streampipes.storage.api.IPipelineElementTemplateStorage;
import org.apache.streampipes.storage.api.IPipelineStorage;
import org.apache.streampipes.storage.api.ISpCoreConfigurationStorage;
import org.apache.streampipes.storage.api.IUserStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import org.springframework.http.ResponseEntity;

public class AbstractRestResource extends AbstractSharedRestInterface {

  protected ISpCoreConfigurationStorage getSpCoreConfigurationStorage() {
    return getNoSqlStorage().getSpCoreConfigurationStorage();
  }

  protected IPipelineElementDescriptionStorage getPipelineElementRdfStorage() {
    return getPipelineElementStorage();
  }

  protected IPipelineElementDescriptionStorage getPipelineElementStorage() {
    return getNoSqlStorage().getPipelineElementDescriptionStorage();
  }

  protected IPipelineStorage getPipelineStorage() {
    return getNoSqlStorage().getPipelineStorageAPI();
  }

  protected IUserStorage getUserStorage() {
    return getNoSqlStorage().getUserStorageAPI();
  }

  protected INotificationStorage getNotificationStorage() {
    return getNoSqlStorage().getNotificationStorageApi();
  }

  protected IPipelineElementTemplateStorage getPipelineElementTemplateStorage() {
    return getNoSqlStorage().getPipelineElementTemplateStorage();
  }

  protected INoSqlStorage getNoSqlStorage() {
    return StorageDispatcher.INSTANCE.getNoSqlStore();
  }

  protected ResponseEntity<Message> constructSuccessMessage(Notification... notifications) {
    return statusMessage(new SuccessMessage(notifications));
  }

  protected ResponseEntity<Message> constructErrorMessage(Notification... notifications) {
    return statusMessage(new ErrorMessage(notifications));
  }

  protected ResponseEntity<Message> statusMessage(Message message) {
    return ResponseEntity.ok().body(message);
  }

  protected ResponseEntity unauthorized() {
    return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED).build();
  }

  protected SpResourceManager getSpResourceManager() {
    return new SpResourceManager();
  }
}
