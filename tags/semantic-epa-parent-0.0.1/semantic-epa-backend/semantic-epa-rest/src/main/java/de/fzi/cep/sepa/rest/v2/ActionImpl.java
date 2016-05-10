package de.fzi.cep.sepa.rest.v2;

import java.net.URISyntaxException;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.shiro.authz.annotation.RequiresAuthentication;

import de.fzi.cep.sepa.messages.Notification;
import de.fzi.cep.sepa.messages.NotificationType;
import de.fzi.cep.sepa.messages.Notifications;
import de.fzi.cep.sepa.model.client.ActionClient;
import de.fzi.cep.sepa.model.impl.graph.SecDescription;
import de.fzi.cep.sepa.rest.api.AbstractRestInterface;
import de.fzi.cep.sepa.rest.api.v2.SepaElementOperation;
import de.fzi.cep.sepa.storage.filter.Filter;
import de.fzi.cep.sepa.storage.util.ClientModelTransformer;

@Path("/v2/users/{username}/actions")
public class ActionImpl extends AbstractRestInterface implements SepaElementOperation {

	@GET
	@Path("/available")
	@RequiresAuthentication
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public String getAvailable(@PathParam("username") String username) {
		List<SecDescription> secs = Filter.byUri(requestor.getAllSECs(), userService.getAvailableActionUris(username));
		List<ActionClient> actionClientElements = ClientModelTransformer.toActionClientModel(secs);
		return toJson(Filter.addFavorites(actionClientElements, userService.getFavoriteActionUris(username)));
	}
	
	@GET
	@Path("/favorites")
	@RequiresAuthentication
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public String getFavorites(@PathParam("username") String username) {
		List<SecDescription> secs = Filter.byUri(requestor.getAllSECs(), userService.getFavoriteActionUris(username));
		return toJson(ClientModelTransformer.toActionClientModel(secs));
	}

	@GET
	@Path("/own")
	@RequiresAuthentication
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public String getOwn(@PathParam("username") String username) {
		List<SecDescription> secs = Filter.byUri(requestor.getAllSECs(), userService.getOwnActionUris(username));
		List<ActionClient> actionClientElements = ClientModelTransformer.toActionClientModel(secs);
		return toJson(Filter.addFavorites(actionClientElements, userService.getFavoriteActionUris(username)));
	}

	@POST
	@Path("/favorites")
	@RequiresAuthentication
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public String addFavorite(@PathParam("username") String username, @FormParam("uri") String elementUri) {
		userService.addActionAsFavorite(username, decode(elementUri));
		return toJson(Notifications.success(NotificationType.OPERATION_SUCCESS));
	}

	@DELETE
	@Path("/favorites/{elementUri}")
	@RequiresAuthentication
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public String removeFavorite(@PathParam("username") String username, @PathParam("elementUri") String elementUri) {
		userService.removeActionFromFavorites(username, decode(elementUri));
		return toJson(Notifications.success(NotificationType.OPERATION_SUCCESS));
	}
	
	@DELETE
	@Path("/own/{elementUri}")
	@RequiresAuthentication
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public String removeOwn(@PathParam("username") String username, @PathParam("elementUri") String elementUri) {
		try {
			userService.deleteOwnAction(username, elementUri);
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
			return toJsonLd(requestor.getSECById(elementUri));
		} catch (URISyntaxException e) {
			return constructErrorMessage(new Notification(NotificationType.UNKNOWN_ERROR.title(), NotificationType.UNKNOWN_ERROR.description(), e.getMessage()));
		}
	}
	
	public static void main(String[] args)
	{
		System.out.println(new ActionImpl().getOwn("riemer@fzi.de"));
	}

	@Path("/{elementUri}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public String getElement(@PathParam("username") String username, @PathParam("elementUri") String elementUri) {
		try {
			return toJson(ClientModelTransformer.toSECClientModel(requestor.getSECById(elementUri)));
		} catch (URISyntaxException e) {
			return toJson(Notifications.create(NotificationType.UNKNOWN_ERROR, e.getMessage()));
		}
	}
	


}
