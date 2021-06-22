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

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.streampipes.commons.exceptions.NoServiceEndpointsAvailableException;
import org.apache.streampipes.commons.exceptions.SepaParseException;
import org.apache.streampipes.manager.assets.AssetManager;
import org.apache.streampipes.manager.endpoint.EndpointItemParser;
import org.apache.streampipes.manager.execution.endpoint.ExtensionsServiceEndpointGenerator;
import org.apache.streampipes.manager.operations.Operations;
import org.apache.streampipes.manager.storage.UserService;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.message.Message;
import org.apache.streampipes.model.message.Notification;
import org.apache.streampipes.model.message.NotificationType;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.storage.api.IPipelineElementDescriptionStorageCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

@Path("/v2/users/{username}/element")
public class PipelineElementImport extends AbstractRestResource {

  private static final Logger LOG = LoggerFactory.getLogger(PipelineElementImport.class);

  @POST
  @Path("/batch")
  @Produces(MediaType.APPLICATION_JSON)
  public Response addBatch(@PathParam("username") String username,
                           @FormParam("uri") String uri,
                           @FormParam("publicElement") boolean publicElement) {
    try {
      uri = URLDecoder.decode(uri, "UTF-8");
      JsonElement element = new JsonParser().parse(parseURIContent(uri, MediaType.APPLICATION_JSON));
      List<Message> messages = new ArrayList<>();
      if (element.isJsonArray()) {
        for (JsonElement jsonObj : element.getAsJsonArray()) {
          messages.add(verifyAndAddElement(jsonObj.getAsString(), username, publicElement));
        }
      }
      return ok(messages);
    } catch (Exception e) {
      e.printStackTrace();
      return statusMessage(Notifications.error(NotificationType.PARSE_ERROR));
    }
  }

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  public Response addElement(@PathParam("username") String username,
                             @FormParam("uri") String uri,
                             @FormParam("publicElement") boolean publicElement) {
    if (!authorized(username)) {
      return ok(Notifications.error(NotificationType.UNAUTHORIZED));
    }
    return ok(verifyAndAddElement(uri, username, publicElement));
  }

  private Message verifyAndAddElement(String uri, String username, boolean publicElement) {
    return new EndpointItemParser().parseAndAddEndpointItem(uri, username, publicElement, true);
  }

  @PUT
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response updateElement(@PathParam("username") String username, @PathParam("id") String elementId) {
    if (!authorized(username)) {
      return ok(Notifications.error(NotificationType.UNAUTHORIZED));
    }
    try {
      NamedStreamPipesEntity entity = find(elementId);
      String url = new ExtensionsServiceEndpointGenerator(entity).getEndpointResourceUrl();
      String payload = parseURIContent(url);
      return ok(Operations.verifyAndUpdateElement(payload, username));
    } catch (URISyntaxException | IOException | SepaParseException | NoServiceEndpointsAvailableException e) {
      e.printStackTrace();
      return constructErrorMessage(new Notification(NotificationType.PARSE_ERROR, e.getMessage()));
    }
  }

  @Path("/{id}")
  @DELETE
  @Produces(MediaType.APPLICATION_JSON)
  public Response deleteElement(@PathParam("username") String username, @PathParam("id") String elementId) {

    UserService userService = getUserService();
    IPipelineElementDescriptionStorageCache requestor = getPipelineElementRdfStorage();
    String appId;
    try {
      if (requestor.existsDataProcessor(elementId)) {
        appId = requestor.getDataProcessorById(elementId).getAppId();
        requestor.deleteDataProcessor(requestor.getDataProcessorById(elementId));
        userService.deleteOwnSepa(username, elementId);
        requestor.refreshDataProcessorCache();
      } else if (requestor.existsDataStream(elementId)) {
        appId = requestor.getDataStreamById(elementId).getAppId();
        requestor.deleteDataStream(requestor.getDataStreamById(elementId));
        userService.deleteOwnSource(username, elementId);
        requestor.refreshDataSourceCache();
      } else if (requestor.existsDataSink(elementId)) {
        appId = requestor.getDataSinkById(elementId).getAppId();
        requestor.deleteDataSink(requestor.getDataSinkById(elementId));
        userService.deleteOwnAction(username, elementId);
        requestor.refreshDataSinkCache();
      } else {
        return constructErrorMessage(new Notification(NotificationType.STORAGE_ERROR.title(),
                NotificationType.STORAGE_ERROR.description()));
      }
      AssetManager.deleteAsset(appId);
    } catch (IOException e) {
      return constructErrorMessage(new Notification(NotificationType.STORAGE_ERROR.title(),
              NotificationType.STORAGE_ERROR.description()));
    }
    return constructSuccessMessage(NotificationType.STORAGE_SUCCESS.uiNotification());
  }

  private NamedStreamPipesEntity find(String elementId) {
    if (getPipelineElementStorage().existsDataSink(elementId)) {
      return getPipelineElementStorage().getDataSinkById(elementId);
    } else if (getPipelineElementStorage().existsDataProcessor(elementId)) {
      return getPipelineElementStorage().getDataProcessorById(elementId);
    } else if (getPipelineElementStorage().existsDataStream(elementId)) {
      return getPipelineElementStorage().getDataStreamById(elementId);
    } else {
      throw new IllegalArgumentException("Could not find element for ID " + elementId);
    }
  }

}
