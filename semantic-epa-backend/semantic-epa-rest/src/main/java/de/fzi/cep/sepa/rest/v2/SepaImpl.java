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

import de.fzi.cep.sepa.messages.NotificationType;
import de.fzi.cep.sepa.messages.Notifications;
import de.fzi.cep.sepa.model.client.ActionClient;
import de.fzi.cep.sepa.model.client.SEPAClient;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.rest.api.AbstractRestInterface;
import de.fzi.cep.sepa.rest.api.v2.SepaElementOperation;
import de.fzi.cep.sepa.storage.filter.Filter;
import de.fzi.cep.sepa.storage.util.ClientModelTransformer;

@Path("/v2/users/{username}/sepas")
public class SepaImpl extends AbstractRestInterface implements SepaElementOperation {

	@GET
	@Path("/available")
	@RequiresAuthentication
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public String getAvailable(@PathParam("username") String username) {
		List<SepaDescription> sepas = Filter.byUri(requestor.getAllSEPAs(), userService.getAvailableSepaUris(username));
		List<SEPAClient> sepaClientElements = ClientModelTransformer.toSEPAClientModel(sepas);
		return toJson(Filter.addFavorites(sepaClientElements,userService.getFavoriteSepaUris(username)));
	}
	
	@GET
	@Path("/favorites")
	@RequiresAuthentication
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public String getFavorites(@PathParam("username") String username) {
		List<SepaDescription> secs = Filter.byUri(requestor.getAllSEPAs(), userService.getFavoriteSepaUris(username));
		return toJson(ClientModelTransformer.toSEPAClientModel(secs));
	}

	@GET
	@Path("/own")
	@RequiresAuthentication
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public String getOwn(@PathParam("username") String username) {
		List<SepaDescription> sepas = Filter.byUri(requestor.getAllSEPAs(), userService.getOwnSepaUris(username));
		List<SEPAClient> sepaClientElements = ClientModelTransformer.toSEPAClientModel(sepas);
		return toJson(Filter.addFavorites(sepaClientElements, userService.getFavoriteSepaUris(username)));
	}

	@POST
	@Path("/favorites")
	@RequiresAuthentication
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public String addFavorite(@PathParam("username") String username, @FormParam("uri") String elementUri) {
		userService.addSepaAsFavorite(username, decode(elementUri));
		return toJson(Notifications.success(NotificationType.OPERATION_SUCCESS));
	}

	@DELETE
	@Path("/favorites/{elementUri}")
	@RequiresAuthentication
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public String removeFavorite(@PathParam("username") String username, @PathParam("elementUri") String elementUri) {
		userService.removeSepaFromFavorites(username, decode(elementUri));
		return toJson(Notifications.success(NotificationType.OPERATION_SUCCESS));
	}
	
	@DELETE
	@Path("/own/{elementUri}")
	@RequiresAuthentication
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public String removeOwn(@PathParam("username") String username, @PathParam("elementUri") String elementUri) {
		try {
			userService.deleteOwnSepa(username, elementUri);
			requestor.deleteSEPA(requestor.getSEPAById(elementUri));
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
			return toJsonLd(requestor.getSEPAById(elementUri));
		} catch (URISyntaxException e) {
			return constructErrorMessage(Notifications.create(NotificationType.UNKNOWN_ERROR, e.getMessage()));
		}
	}
	
	public static void main(String[] args)
	{
		System.out.println(new SepaImpl().getOwn("riemer@fzi.de"));
	}

	@Path("/{elementUri}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public String getElement(String username, String elementUri) {
		// TODO Access rights
		try {
			return toJson(ClientModelTransformer.toSEPAClientModel(requestor.getSEPAById(elementUri)));
		} catch (URISyntaxException e) {
			return toJson(Notifications.create(NotificationType.UNKNOWN_ERROR, e.getMessage()));
		}
	}

}
