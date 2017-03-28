package de.fzi.cep.sepa.rest.impl;

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

import de.fzi.cep.sepa.model.impl.graph.SecInvocation;
import de.fzi.cep.sepa.rest.annotation.GsonWithIds;
import de.fzi.cep.sepa.storage.api.StorageRequests;
import de.fzi.cep.sepa.storage.service.UserService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;

import de.fzi.cep.sepa.model.client.messages.Notification;
import de.fzi.cep.sepa.model.client.messages.NotificationType;
import de.fzi.cep.sepa.model.client.messages.Notifications;
import de.fzi.cep.sepa.model.impl.graph.SecDescription;
import de.fzi.cep.sepa.rest.api.IPipelineElement;
import de.fzi.cep.sepa.storage.filter.Filter;

@Path("/v2/users/{username}/actions")
public class SemanticEventConsumer extends AbstractRestInterface implements IPipelineElement {

	@GET
	@Path("/available")
	@RequiresAuthentication
	@Produces(MediaType.APPLICATION_JSON)
	@GsonWithIds
	@Override
	public Response getAvailable(@PathParam("username") String username) {
		List<SecDescription> secs = Filter.byUri(getPipelineElementRdfStorage().getAllSECs(),
				getUserService().getAvailableActionUris(username));
		return ok(secs);
	}
	
	@GET
	@Path("/favorites")
	@RequiresAuthentication
	@Produces(MediaType.APPLICATION_JSON)
	@GsonWithIds
	@Override
	public Response getFavorites(@PathParam("username") String username) {
		List<SecDescription> secs = Filter.byUri(getPipelineElementRdfStorage().getAllSECs(),
				getUserService().getFavoriteActionUris(username));
		return ok(secs);
	}

	@GET
	@Path("/own")
	@RequiresAuthentication
	@Produces(MediaType.APPLICATION_JSON)
	@GsonWithIds
	@Override
	public Response getOwn(@PathParam("username") String username) {
		List<SecDescription> secs = Filter.byUri(getPipelineElementRdfStorage().getAllSECs(),
				getUserService().getOwnActionUris(username));
		List<SecInvocation> si = secs.stream().map(s -> new SecInvocation(new SecInvocation(s))).collect(Collectors.toList());
		return ok(si);
	}

	@POST
	@Path("/favorites")
	@RequiresAuthentication
	@Produces(MediaType.APPLICATION_JSON)
	@GsonWithIds
	@Override
	public Response addFavorite(@PathParam("username") String username, @FormParam("uri") String elementUri) {
		getUserService().addActionAsFavorite(username, decode(elementUri));
		return statusMessage(Notifications.success(NotificationType.OPERATION_SUCCESS));
	}

	@DELETE
	@Path("/favorites/{elementUri}")
	@RequiresAuthentication
	@Produces(MediaType.APPLICATION_JSON)
	@GsonWithIds
	@Override
	public Response removeFavorite(@PathParam("username") String username, @PathParam("elementUri") String elementUri) {
		getUserService().removeActionFromFavorites(username, decode(elementUri));
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
			StorageRequests requestor = getPipelineElementRdfStorage();
			getUserService().deleteOwnAction(username, elementUri);
			requestor.deleteSEC(requestor.getSECById(elementUri));
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
			return toJsonLd(getPipelineElementRdfStorage().getSECById(elementUri));
		} catch (URISyntaxException e) {
			return toJson(constructErrorMessage(new Notification(NotificationType.UNKNOWN_ERROR.title(), NotificationType.UNKNOWN_ERROR.description(), e.getMessage())));
		}
	}
	
	public static void main(String[] args)
	{
		System.out.println(new SemanticEventConsumer().getOwn("riemer@fzi.de"));
	}

	@Path("/{elementUri}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@GsonWithIds
	@Override
	public Response getElement(@PathParam("username") String username, @PathParam("elementUri") String elementUri) {
		try {
			return ok(new SecInvocation(new SecInvocation(getPipelineElementRdfStorage().getSECById(elementUri))));
		} catch (URISyntaxException e) {
			return statusMessage(Notifications.error(NotificationType.UNKNOWN_ERROR, e.getMessage()));
		}
	}
	


}
