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

import org.streampipes.manager.endpoint.EndpointItemParser;
import org.streampipes.manager.storage.UserService;
import org.streampipes.model.client.messages.Message;
import org.streampipes.model.client.messages.NotificationType;
import org.streampipes.model.client.messages.Notification;
import org.streampipes.rest.impl.AbstractRestInterface;
import org.streampipes.storage.api.IPipelineElementDescriptionStorage;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URISyntaxException;

@Path("/v2/noauth/users/{username}/element")
public class PipelineElementImportNoUser extends AbstractRestInterface {

	@Path("/")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response addElement(@PathParam("username") String username, @FormParam("uri") String uri, @FormParam("publicElement") boolean publicElement)
    {
//        if (!authorized(username)) return ok(Notifications.error(NotificationType.UNAUTHORIZED));
        return ok(verifyAndAddElement(uri, username, publicElement));
    }

    private Message verifyAndAddElement(String uri, String username, boolean publicElement) {
        return new EndpointItemParser().parseAndAddEndpointItem(uri, username, publicElement);
    }

    @Path("/{id}")
    @DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteElement(@PathParam("username") String username, @PathParam("id") String elementId) {

		UserService userService = getUserService();
		IPipelineElementDescriptionStorage requestor = getPipelineElementRdfStorage();
		try {
			if (requestor.getSEPById(elementId) != null)
				{
					requestor.deleteSEP(requestor.getSEPById(elementId));
					userService.deleteOwnSource(username, elementId);
				}
			else return constructErrorMessage(new Notification(NotificationType.STORAGE_ERROR.title(), NotificationType.STORAGE_ERROR.description()));
		} catch (URISyntaxException e) {
			return constructErrorMessage(new Notification(NotificationType.STORAGE_ERROR.title(), NotificationType.STORAGE_ERROR.description()));
		}
		return constructSuccessMessage(NotificationType.STORAGE_SUCCESS.uiNotification());
	}

}
