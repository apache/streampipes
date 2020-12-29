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

package org.apache.streampipes.rest.impl;

import io.fogsy.empire.core.empire.annotation.InvalidRdfException;
import org.apache.http.client.ClientProtocolException;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.streampipes.commons.Utils;
import org.apache.streampipes.manager.endpoint.HttpJsonParser;
import org.apache.streampipes.manager.storage.UserManagementService;
import org.apache.streampipes.manager.storage.UserService;
import org.apache.streampipes.model.base.AbstractStreamPipesEntity;
import org.apache.streampipes.model.base.StreamPipesJsonLdContainer;
import org.apache.streampipes.model.message.Notification;
import org.apache.streampipes.model.message.*;
import org.apache.streampipes.serializers.json.GsonSerializer;
import org.apache.streampipes.serializers.jsonld.JsonLdTransformer;
import org.apache.streampipes.storage.api.*;
import org.apache.streampipes.storage.management.StorageDispatcher;
import org.apache.streampipes.storage.management.StorageManager;
import org.eclipse.rdf4j.rio.RDFHandlerException;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.List;

public abstract class AbstractRestInterface {

  protected <T> String toJsonLd(T object) {
    try {
      return Utils.asString(new JsonLdTransformer().toJsonLd(object));
    } catch (RDFHandlerException | IllegalArgumentException
            | IllegalAccessException | SecurityException | InvocationTargetException
            | ClassNotFoundException | InvalidRdfException e) {
      return toJson(constructErrorMessage(new Notification(NotificationType.UNKNOWN_ERROR.title(),
              NotificationType.UNKNOWN_ERROR.description(),
              e.getMessage())));
    }
  }

  protected <T> String toJsonLd(String rootElementUri, T object) {
    try {
      return Utils.asString(new JsonLdTransformer(rootElementUri).toJsonLd(object));
    } catch (IllegalAccessException | InvocationTargetException | InvalidRdfException | ClassNotFoundException e) {
      return toJson(constructErrorMessage(new Notification(NotificationType.UNKNOWN_ERROR.title(),
              NotificationType.UNKNOWN_ERROR.description(),
              e.getMessage())));
    }
  }

  protected IPipelineElementDescriptionStorageCache getPipelineElementRdfStorage() {
    return StorageManager.INSTANCE.getPipelineElementStorage();
  }

  protected IPipelineStorage getPipelineStorage() {
    return getNoSqlStorage().getPipelineStorageAPI();
  }

  protected IUserStorage getUserStorage() {
    return getNoSqlStorage().getUserStorageAPI();
  }

  protected UserService getUserService() {
    return UserManagementService.getUserService();
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

  protected INoSqlStorage getNoSqlStorage() {
    return StorageDispatcher.INSTANCE.getNoSqlStore();
  }

  protected IFileMetadataStorage getFileMetadataStorage() {
    return getNoSqlStorage().getFileMetadataStorage();
  }

  protected ITripleStorage getTripleStorage() {
    return StorageDispatcher.INSTANCE.getTripleStore();
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

  protected String getCurrentUsername() throws AuthenticationException {
    if (SecurityUtils.getSubject().isAuthenticated()) {
      return SecurityUtils.getSubject().getPrincipal().toString();
    }
    throw new AuthenticationException("Not authenticated");
  }

  protected boolean authorized(String username) {
    return username.equals(SecurityUtils.getSubject().getPrincipal().toString());
  }

  protected boolean isAuthenticated() {
    return SecurityUtils.getSubject().isAuthenticated();
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

  protected <T> Response ok(T entity) {
    return Response
            .ok(entity)
            .build();
  }

  protected <T> Response badRequest(T entity) {
    return Response
            .status(400)
            .entity(entity)
            .build();
  }

  protected <T> Response serverError(T entity) {
    return Response
            .status(500)
            .entity(entity)
            .build();
  }

  protected StreamPipesJsonLdContainer asContainer(List<? extends AbstractStreamPipesEntity> elements) {
    return new StreamPipesJsonLdContainer(elements);
  }

  protected Response ok() {
    return Response.ok().build();
  }

  protected Response fail() {
    return Response.serverError().build();
  }

  protected <T> String toJson(T element) {
    return GsonSerializer.getGson().toJson(element);
  }

}
