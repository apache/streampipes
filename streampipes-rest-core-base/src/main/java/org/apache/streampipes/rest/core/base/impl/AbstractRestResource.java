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

import org.apache.streampipes.manager.endpoint.HttpJsonParser;
import org.apache.streampipes.model.message.ErrorMessage;
import org.apache.streampipes.model.message.Message;
import org.apache.streampipes.model.message.Notification;
import org.apache.streampipes.model.message.SuccessMessage;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.rest.shared.impl.AbstractSharedRestInterface;
import org.apache.streampipes.storage.api.IDataLakeStorage;
import org.apache.streampipes.storage.api.IFileMetadataStorage;
import org.apache.streampipes.storage.api.INoSqlStorage;
import org.apache.streampipes.storage.api.INotificationStorage;
import org.apache.streampipes.storage.api.IPipelineElementDescriptionStorageCache;
import org.apache.streampipes.storage.api.IPipelineElementTemplateStorage;
import org.apache.streampipes.storage.api.IPipelineStorage;
import org.apache.streampipes.storage.api.ISpCoreConfigurationStorage;
import org.apache.streampipes.storage.api.IUserStorage;
import org.apache.streampipes.storage.api.IVisualizationStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;
import org.apache.streampipes.storage.management.StorageManager;

import org.apache.http.client.ClientProtocolException;

import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;

public abstract class AbstractRestResource extends AbstractSharedRestInterface {

  protected ISpCoreConfigurationStorage getSpCoreConfigurationStorage() {
    return getNoSqlStorage().getSpCoreConfigurationStorage();
  }

  protected IPipelineElementDescriptionStorageCache getPipelineElementRdfStorage() {
    return StorageManager.INSTANCE.getPipelineElementStorage();
  }

  protected IPipelineElementDescriptionStorageCache getPipelineElementStorage() {
    return getNoSqlStorage().getPipelineElementDescriptionStorage();
  }

  protected IPipelineStorage getPipelineStorage() {
    return getNoSqlStorage().getPipelineStorageAPI();
  }

  protected IUserStorage getUserStorage() {
    return getNoSqlStorage().getUserStorageAPI();
  }

  protected IVisualizationStorage getVisualizationStorage() {
    return getNoSqlStorage().getVisualizationStorageApi();
  }

  protected INotificationStorage getNotificationStorage() {
    return getNoSqlStorage().getNotificationStorageApi();
  }

  protected IDataLakeStorage getDataLakeStorage() {
    return getNoSqlStorage().getDataLakeStorage();
  }

  protected IPipelineElementTemplateStorage getPipelineElementTemplateStorage() {
    return getNoSqlStorage().getPipelineElementTemplateStorage();
  }

  protected INoSqlStorage getNoSqlStorage() {
    return StorageDispatcher.INSTANCE.getNoSqlStore();
  }

  protected IFileMetadataStorage getFileMetadataStorage() {
    return getNoSqlStorage().getFileMetadataStorage();
  }

  protected String parseURIContent(String payload) throws URISyntaxException,
      ClientProtocolException, IOException {
    return parseURIContent(payload, null);
  }

  protected String parseURIContent(String payload, String mediaType) throws URISyntaxException,
      ClientProtocolException, IOException {
    URI uri = new URI(payload);
    return HttpJsonParser.getContentFromUrl(uri, mediaType);
  }

  protected Response constructSuccessMessage(Notification... notifications) {
    return statusMessage(new SuccessMessage(notifications));
  }

  protected Response constructErrorMessage(Notification... notifications) {
    return statusMessage(new ErrorMessage(notifications));
  }

  @SuppressWarnings("deprecation")
  protected String decode(String encodedString) {
    return URLDecoder.decode(encodedString);
  }

  protected Response statusMessage(Message message) {
    return Response
        .ok()
        .entity(message)
        .build();
  }

  protected Response statusMessage(Message message, Response.ResponseBuilder builder) {
    return builder
        .entity(message)
        .build();
  }

  protected Response unauthorized() {
    return Response.status(Response.Status.UNAUTHORIZED).build();
  }

  protected SpResourceManager getSpResourceManager() {
    return new SpResourceManager();
  }

}
