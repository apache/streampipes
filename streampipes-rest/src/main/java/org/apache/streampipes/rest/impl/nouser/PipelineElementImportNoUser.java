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

package org.apache.streampipes.rest.impl.nouser;

import org.apache.streampipes.manager.endpoint.EndpointItemParser;
import org.apache.streampipes.manager.storage.UserService;
import org.apache.streampipes.model.message.Message;
import org.apache.streampipes.model.message.Notification;
import org.apache.streampipes.model.message.NotificationType;
import org.apache.streampipes.rest.impl.AbstractRestInterface;
import org.apache.streampipes.storage.api.IPipelineElementDescriptionStorageCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/v2/noauth/users/{username}/element")
public class PipelineElementImportNoUser extends AbstractRestInterface {

  private static final Logger logger = LoggerFactory.getLogger(PipelineElementImportNoUser.class);

  @Context
  UriInfo uri;

  @Path("/")
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  public Response addElement(@PathParam("username") String username, @FormParam("uri") String uri, @FormParam("publicElement") boolean publicElement) {
    logger.info("User " + username + " adds element with URI: " + uri + " to triplestore");

    return ok(verifyAndAddElement(uri, username, publicElement));
  }

  private Message verifyAndAddElement(String uri, String username, boolean publicElement) {
    return new EndpointItemParser().parseAndAddEndpointItem(uri, username, publicElement, true);
  }

  @Path("/delete")
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  public Response deleteElement(@PathParam("username") String username, @FormParam("uri") String uri) {

    UserService userService = getUserService();
    IPipelineElementDescriptionStorageCache requestor = getPipelineElementRdfStorage();

    logger.info("User " + username + " deletes element with URI: " + uri + " from triplestore");

    if (requestor.getDataSourceById(uri) != null) {
      requestor.deleteDataSource(requestor.getDataSourceById(uri));
      userService.deleteOwnSource(username, uri);
      requestor.refreshDataSourceCache();
    } else {
      return constructErrorMessage(new Notification(NotificationType.STORAGE_ERROR.title(), NotificationType.STORAGE_ERROR.description()));
    }
    return constructSuccessMessage(NotificationType.STORAGE_SUCCESS.uiNotification());
  }

}
