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

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.streampipes.model.client.messages.Notification;
import org.apache.streampipes.model.client.messages.NotificationType;
import org.apache.streampipes.model.client.messages.Notifications;
import org.apache.streampipes.model.graph.DataSourceDescription;
import org.apache.streampipes.rest.api.IPipelineElement;
import org.apache.streampipes.rest.shared.annotation.GsonWithIds;
import org.apache.streampipes.storage.rdf4j.filter.Filter;

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

@Path("/v2/users/{username}/sources")
public class SemanticEventProducer extends AbstractRestInterface implements IPipelineElement {

	@Path("/{sourceId}/streams")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@GsonWithIds
	public Response getStreamsBySource(@PathParam("username") String username, @PathParam("sourceId") String sourceId)
	{
		try {
			return ok(new DataSourceDescription(getPipelineElementRdfStorage().getDataSourceById(sourceId)));
		} catch (URISyntaxException e) {
			return constructErrorMessage(new Notification(NotificationType.URIOFFLINE.title(),
					NotificationType.URIOFFLINE.description(), e.getMessage()));
		} catch (Exception e)
		{
			return constructErrorMessage(new Notification(NotificationType.UNKNOWN_ERROR.title(),
					NotificationType.UNKNOWN_ERROR.description(), e.getMessage()));
		}
		
	}
	
	@GET
	@Path("/available")
	@RequiresAuthentication
	@Produces(MediaType.APPLICATION_JSON)
	@GsonWithIds
	@Override
	public Response getAvailable(@PathParam("username") String username) {
		List<DataSourceDescription> seps = Filter.byUri(getPipelineElementRdfStorage().getAllDataSources(),
				getUserService().getAvailableSourceUris(username));
		return ok(seps);
	}
	
	@GET
	@Path("/favorites")
	@RequiresAuthentication
	@Produces(MediaType.APPLICATION_JSON)
	@GsonWithIds
	@Override
	public Response getFavorites(@PathParam("username") String username) {
		List<DataSourceDescription> seps = Filter.byUri(getPipelineElementRdfStorage().getAllDataSources(),
				getUserService().getFavoriteSourceUris(username));
		return ok(seps);
	}

	@GET
	@Path("/own")
	@RequiresAuthentication
	@Produces(MediaType.APPLICATION_JSON)
	@GsonWithIds
	@Override
	public Response getOwn(@PathParam("username") String username) {
		List<DataSourceDescription> seps = Filter.byUri(getPipelineElementRdfStorage().getAllDataSources(),
				getUserService().getOwnSourceUris(username));
		List<DataSourceDescription> si = seps.stream().map(s -> new DataSourceDescription(s)).collect(Collectors.toList());

		return ok(si);
	}

	@POST
	@Path("/favorites")
	@RequiresAuthentication
	@Produces(MediaType.APPLICATION_JSON)
	@GsonWithIds
	@Override
	public Response addFavorite(@PathParam("username") String username, @FormParam("uri") String elementUri) {
		getUserService().addSourceAsFavorite(username, decode(elementUri));
		return statusMessage(Notifications.success(NotificationType.OPERATION_SUCCESS));
	}

	@DELETE
	@Path("/favorites/{elementUri}")
	@RequiresAuthentication
	@Produces(MediaType.APPLICATION_JSON)
	@GsonWithIds
	@Override
	public Response removeFavorite(@PathParam("username") String username, @PathParam("elementUri") String elementUri) {
		getUserService().removeSourceFromFavorites(username, decode(elementUri));
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
			getUserService().deleteOwnSource(username, elementUri);
			getPipelineElementRdfStorage().deleteDataSink(getPipelineElementRdfStorage().getDataSinkById(elementUri));
		} catch (URISyntaxException e) {
			return constructErrorMessage(new Notification(NotificationType.STORAGE_ERROR.title(), NotificationType.STORAGE_ERROR.description(), e.getMessage()));
		}
		return constructSuccessMessage(NotificationType.STORAGE_SUCCESS.uiNotification());
	}

	@Path("/{elementUri}/jsonld")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Override
	public String getAsJsonLd(@PathParam("elementUri") String elementUri) {
		try {
			return toJsonLd(getPipelineElementRdfStorage().getDataSinkById(elementUri));
		} catch (URISyntaxException e) {
			return toJson(statusMessage(Notifications.error(NotificationType.UNKNOWN_ERROR)));
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
			return ok(new DataSourceDescription(getPipelineElementRdfStorage().getDataSourceById(elementUri)));
		} catch (URISyntaxException e) {
			return statusMessage(Notifications.error(NotificationType.UNKNOWN_ERROR, e.getMessage()));
		}
	}

}
