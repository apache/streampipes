/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.rest.impl.nouser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.manager.endpoint.EndpointItemParser;
import org.streampipes.manager.storage.UserService;
import org.streampipes.model.client.messages.Message;
import org.streampipes.model.client.messages.NotificationType;
import org.streampipes.model.client.messages.Notification;
import org.streampipes.rest.impl.AbstractRestInterface;
import org.streampipes.storage.api.IPipelineElementDescriptionStorage;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.net.URISyntaxException;

@Path("/v2/noauth/users/{username}/element")
public class PipelineElementImportNoUser extends AbstractRestInterface {

	private static final Logger logger = LoggerFactory.getLogger(PipelineElementImportNoUser.class);

	@Context
	UriInfo uri;

	@Path("/")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response addElement(@PathParam("username") String username, @FormParam("uri") String uri, @FormParam("publicElement") boolean publicElement)
    {

//		URI myUri = uri.getBaseUri();
//		String id = myUri.toString()  + "v2/adapter/all/" + elementId;

		logger.info("User " + username + " adds element with URI: " + uri + " to triplestore");

        return ok(verifyAndAddElement(uri, username, publicElement));
    }

    private Message verifyAndAddElement(String uri, String username, boolean publicElement) {
        return new EndpointItemParser().parseAndAddEndpointItem(uri, username, publicElement);
    }

    @Path("/delete")
    @POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteElement(@PathParam("username") String username, @FormParam("uri") String uri) {

//		URI myUri = uri.getBaseUri();
//		String id = myUri.toString()  + "v2/adapter/all/" + elementId;


		UserService userService = getUserService();
		IPipelineElementDescriptionStorage requestor = getPipelineElementRdfStorage();

		logger.info("User " + username + " deletes element with URI: " + uri + " from triplestore");

		try {
			if (requestor.getSEPById(uri) != null)
				{
					requestor.deleteSEP(requestor.getSEPById(uri));
					userService.deleteOwnSource(username, uri);
				}
			else return constructErrorMessage(new Notification(NotificationType.STORAGE_ERROR.title(), NotificationType.STORAGE_ERROR.description()));
		} catch (URISyntaxException e) {
			return constructErrorMessage(new Notification(NotificationType.STORAGE_ERROR.title(), NotificationType.STORAGE_ERROR.description()));
		}
		return constructSuccessMessage(NotificationType.STORAGE_SUCCESS.uiNotification());
	}

}
