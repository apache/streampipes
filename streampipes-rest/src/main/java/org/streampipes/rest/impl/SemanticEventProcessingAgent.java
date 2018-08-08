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

package org.streampipes.rest.impl;

import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.rest.shared.serializer.annotation.GsonWithIds;
import org.apache.shiro.authz.annotation.RequiresAuthentication;

import org.streampipes.model.client.messages.NotificationType;
import org.streampipes.model.client.messages.Notifications;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.rest.api.IPipelineElement;
import org.streampipes.storage.rdf4j.filter.Filter;

@Path("/v2/users/{username}/sepas")
public class SemanticEventProcessingAgent extends AbstractRestInterface implements IPipelineElement {

	@GET
	@Path("/available")
	@RequiresAuthentication
	@Produces(MediaType.APPLICATION_JSON)
	@GsonWithIds
	@Override
	public Response getAvailable(@PathParam("username") String username) {
		List<DataProcessorDescription> sepas = Filter.byUri(getPipelineElementRdfStorage().getAllSEPAs(),
				getUserService().getAvailableSepaUris(username));
		return ok(sepas);
	}
	
	@GET
	@Path("/favorites")
	@RequiresAuthentication
	@Produces(MediaType.APPLICATION_JSON)
	@GsonWithIds
	@Override
	public Response getFavorites(@PathParam("username") String username) {
		List<DataProcessorDescription> sepas = Filter.byUri(getPipelineElementRdfStorage().getAllSEPAs(),
				getUserService().getFavoriteSepaUris(username));
		return ok(sepas);
	}

	@GET
	@Path("/own")
	@RequiresAuthentication
	@Produces(MediaType.APPLICATION_JSON)
	@GsonWithIds
	@Override
	public Response getOwn(@PathParam("username") String username) {
		List<DataProcessorDescription> sepas = Filter.byUri(getPipelineElementRdfStorage().getAllSEPAs(),
				getUserService().getOwnSepaUris(username));
		List<DataProcessorInvocation> si = sepas.stream().map(s -> new DataProcessorInvocation(new DataProcessorInvocation(s))).collect(Collectors.toList());

		return ok(si);
	}

	@POST
	@Path("/favorites")
	@RequiresAuthentication
	@Produces(MediaType.APPLICATION_JSON)
	@GsonWithIds
	@Override
	public Response addFavorite(@PathParam("username") String username, @FormParam("uri") String elementUri) {
		getUserService().addSepaAsFavorite(username, decode(elementUri));
		return statusMessage(Notifications.success(NotificationType.OPERATION_SUCCESS));
	}

	@DELETE
	@Path("/favorites/{elementUri}")
	@RequiresAuthentication
	@Produces(MediaType.APPLICATION_JSON)
	@GsonWithIds
	@Override
	public Response removeFavorite(@PathParam("username") String username, @PathParam("elementUri") String elementUri) {
		getUserService().removeSepaFromFavorites(username, decode(elementUri));
		return statusMessage(Notifications.success(NotificationType.OPERATION_SUCCESS));
	}
	
	@DELETE
	@Path("/own/{elementUri}")
	@RequiresAuthentication
	@Produces(MediaType.APPLICATION_JSON)
	@GsonWithIds
	@Override
	public Response removeOwn(@PathParam("username") String username, @PathParam("elementUri") String elementUri) {
		try {
			getUserService().deleteOwnSepa(username, elementUri);
			getPipelineElementRdfStorage().deleteSEPA(getPipelineElementRdfStorage().getSEPAById(elementUri));
		} catch (URISyntaxException e) {
			return constructErrorMessage(Notifications.create(NotificationType.STORAGE_ERROR, e.getMessage()));
		}
		return constructSuccessMessage(NotificationType.STORAGE_SUCCESS.uiNotification());
	}

	@Path("/{elementUri}/jsonld")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Override
	public String getAsJsonLd(@PathParam("elementUri") String elementUri) {
		try {
			return toJsonLd(getPipelineElementRdfStorage().getSEPAById(elementUri));
		} catch (URISyntaxException e) {
			return toJson(constructErrorMessage(Notifications.create(NotificationType.UNKNOWN_ERROR, e.getMessage())));
		}
	}

	@Path("/{elementUri}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@GsonWithIds
	@Override
	public Response getElement(@PathParam("username") String username, @PathParam("elementUri") String elementUri) {
		// TODO Access rights
		try {
			return ok(new DataProcessorInvocation(new DataProcessorInvocation(getPipelineElementRdfStorage().getSEPAById(elementUri))));
		} catch (URISyntaxException e) {
			return statusMessage(Notifications.error(NotificationType.UNKNOWN_ERROR, e.getMessage()));
		}
	}

}
