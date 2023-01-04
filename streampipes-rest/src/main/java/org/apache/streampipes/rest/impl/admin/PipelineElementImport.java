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

package org.apache.streampipes.rest.impl.admin;

import org.apache.streampipes.commons.exceptions.NoServiceEndpointsAvailableException;
import org.apache.streampipes.commons.exceptions.SepaParseException;
import org.apache.streampipes.manager.assets.AssetManager;
import org.apache.streampipes.manager.endpoint.EndpointItemParser;
import org.apache.streampipes.manager.execution.endpoint.ExtensionsServiceEndpointGenerator;
import org.apache.streampipes.manager.operations.Operations;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.message.Message;
import org.apache.streampipes.model.message.Notification;
import org.apache.streampipes.model.message.NotificationType;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.rest.security.AuthConstants;
import org.apache.streampipes.storage.api.IPipelineElementDescriptionStorageCache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.net.URISyntaxException;

@Path("/v2/element")
@Component
@PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
public class PipelineElementImport extends AbstractAuthGuardedRestResource {

  private static final Logger LOG = LoggerFactory.getLogger(PipelineElementImport.class);

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  public Response addElement(@FormParam("uri") String uri,
                             @FormParam("publicElement") boolean publicElement) {
    return ok(verifyAndAddElement(uri, getAuthenticatedUserSid(), publicElement));
  }

  @PUT
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response updateElement(@PathParam("id") String elementId) {
    try {
      NamedStreamPipesEntity entity = find(elementId);
      String url = new ExtensionsServiceEndpointGenerator(entity).getEndpointResourceUrl();
      String payload = parseURIContent(url);
      return ok(Operations.verifyAndUpdateElement(payload));
    } catch (URISyntaxException | IOException | SepaParseException | NoServiceEndpointsAvailableException e) {
      e.printStackTrace();
      return constructErrorMessage(new Notification(NotificationType.PARSE_ERROR, e.getMessage()));
    }
  }

  @Path("/{id}")
  @DELETE
  @Produces(MediaType.APPLICATION_JSON)
  public Response deleteElement(@PathParam("id") String elementId) {
    IPipelineElementDescriptionStorageCache requestor = getPipelineElementRdfStorage();
    String appId;
    try {
      if (requestor.existsDataProcessor(elementId)) {
        appId = requestor.getDataProcessorById(elementId).getAppId();
        getSpResourceManager().manageDataProcessors().delete(elementId);
      } else if (requestor.existsDataStream(elementId)) {
        appId = requestor.getDataStreamById(elementId).getAppId();
        getSpResourceManager().manageDataStreams().delete(elementId);
      } else if (requestor.existsDataSink(elementId)) {
        appId = requestor.getDataSinkById(elementId).getAppId();
        getSpResourceManager().manageDataSinks().delete(elementId);
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

  private Message verifyAndAddElement(String uri,
                                      String principalSid,
                                      boolean publicElement) {
    return new EndpointItemParser().parseAndAddEndpointItem(uri, principalSid, publicElement);
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
