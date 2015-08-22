package de.fzi.cep.sepa.rest.v2;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLDecoder;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.fzi.cep.sepa.commons.exceptions.SepaParseException;
import de.fzi.cep.sepa.manager.operations.Operations;
import de.fzi.cep.sepa.messages.Notification;
import de.fzi.cep.sepa.messages.NotificationType;
import de.fzi.cep.sepa.messages.Notifications;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.rest.api.AbstractRestInterface;

import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/v2/users/{username}/element")
public class PipelineElementImpl extends AbstractRestInterface {

	static Logger  LOG = LoggerFactory.getLogger(PipelineElementImpl.class);

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public String addElement(@PathParam("username") String username, @FormParam("uri") String uri, @FormParam("publicElement") boolean publicElement)
	{
		if (!authorized(username)) return toJson(Notifications.error(NotificationType.UNAUTHORIZED));
		try {
			uri = URLDecoder.decode(uri, "UTF-8");
			String payload = parseURIContent(uri);
			return toJson(Operations.verifyAndAddElement(payload, username, publicElement));
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
			return constructErrorMessage(new Notification(NotificationType.PARSE_ERROR.title(), NotificationType.PARSE_ERROR.description(), e.getMessage()));
		} catch (SepaParseException e) {
			e.printStackTrace();
			return constructErrorMessage(new Notification(NotificationType.PARSE_ERROR.title(), NotificationType.PARSE_ERROR.description(), e.getMessage()));
		}
	}
	
	@PUT
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public String updateElement(@PathParam("username") String username, @PathParam("id") String uri)
	{
		if (!authorized(username)) return toJson(Notifications.error(NotificationType.UNAUTHORIZED));
		try {
			uri = URLDecoder.decode(uri, "UTF-8");
			String payload = parseURIContent(uri);
			return toJson(Operations.verifyAndUpdateElement(payload, username));
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
			return constructErrorMessage(new Notification(NotificationType.PARSE_ERROR.title(), NotificationType.PARSE_ERROR.description(), e.getMessage()));
		} catch (SepaParseException e) {
			e.printStackTrace();
			return constructErrorMessage(new Notification(NotificationType.PARSE_ERROR.title(), NotificationType.PARSE_ERROR.description(), e.getMessage()));
		}
	}
	
	@Path("/{id}")
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteElement(@PathParam("username") String username, @PathParam("id") String elementId) {
		
		try {
			if (requestor.getSEPAById(elementId) != null) 
				{
					requestor.deleteSEPA(requestor.getSEPAById(elementId));
					userService.deleteOwnSepa(username, elementId);
				}
			else if (requestor.getSEPById(elementId) != null) 
				{
					requestor.deleteSEP(requestor.getSEPById(elementId));
					userService.deleteOwnSource(username, elementId);
				}
			else if (requestor.getSECById(elementId) != null) 
				{
					requestor.deleteSEC(requestor.getSECById(elementId));
					userService.deleteOwnAction(username, elementId);
				}
			else return constructErrorMessage(new Notification(NotificationType.STORAGE_ERROR.title(), NotificationType.STORAGE_ERROR.description()));
		} catch (URISyntaxException e) {
			return constructErrorMessage(new Notification(NotificationType.STORAGE_ERROR.title(), NotificationType.STORAGE_ERROR.description()));
		}
		return constructSuccessMessage(NotificationType.STORAGE_SUCCESS.uiNotification());
	}
	
	@Path("{id}/jsonld")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getActionAsJsonLd(@PathParam("id") String elementId)
	{
		elementId = decode(elementId);
		try {
			if (requestor.getSEPAById(elementId) != null) return toJsonLd(requestor.getSEPAById(elementId));
			else if (requestor.getSEPById(elementId) != null) return toJsonLd(requestor.getSEPById(elementId));
			else if (requestor.getSECById(elementId) != null) return toJsonLd(requestor.getSECById(elementId));
			else return toJson(Notifications.create(NotificationType.UNKNOWN_ERROR));
		} catch (URISyntaxException e) {
			return constructErrorMessage(new Notification(NotificationType.UNKNOWN_ERROR.title(), NotificationType.UNKNOWN_ERROR.description(), e.getMessage()));
		}
	}
}
