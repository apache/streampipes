package org.streampipes.rest.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.commons.exceptions.SepaParseException;
import org.streampipes.manager.operations.Operations;
import org.streampipes.model.client.messages.Message;
import org.streampipes.model.client.messages.Notification;
import org.streampipes.model.client.messages.NotificationType;
import org.streampipes.model.client.messages.Notifications;
import org.streampipes.storage.api.StorageRequests;
import org.streampipes.storage.service.UserService;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/v2/users/{username}/element")
public class PipelineElementImport extends AbstractRestInterface {

	static Logger  LOG = LoggerFactory.getLogger(PipelineElementImport.class);

	@POST
	@Path("/batch")
	@Produces(MediaType.APPLICATION_JSON)
	public Response addBatch(@PathParam("username") String username, @FormParam("uri") String uri, @FormParam("publicElement") boolean publicElement)
	{
		try {
			uri = URLDecoder.decode(uri, "UTF-8");
			JsonElement element = new JsonParser().parse(parseURIContent(uri, "application/json"));
			List<Message> messages = new ArrayList<>();
			if (element.isJsonArray()) {
				for(JsonElement jsonObj : element.getAsJsonArray()) {
					 messages.add(verifyAndAddElement(jsonObj.getAsString(), username, publicElement));
				}
			}
			return ok(messages);
		} catch (Exception e) {
			e.printStackTrace();
			return statusMessage(Notifications.error(NotificationType.PARSE_ERROR));
		}
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response addElement(@PathParam("username") String username, @FormParam("uri") String uri, @FormParam("publicElement") boolean publicElement)
	{
		if (!authorized(username)) return ok(Notifications.error(NotificationType.UNAUTHORIZED));
		return ok(verifyAndAddElement(uri, username, publicElement));
	}

	private Message verifyAndAddElement(String uri, String username, boolean publicElement) {
		try {
			uri = URLDecoder.decode(uri, "UTF-8");
			String payload = parseURIContent(uri);
			return Operations.verifyAndAddElement(payload, username, publicElement);
		} catch (Exception e) {
            e.printStackTrace();
			return Notifications.error(NotificationType.PARSE_ERROR);

		}
	}
	
	@PUT
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateElement(@PathParam("username") String username, @PathParam("id") String uri)
	{
		if (!authorized(username)) return ok(Notifications.error(NotificationType.UNAUTHORIZED));
		try {
			uri = URLDecoder.decode(uri, "UTF-8");
			String payload = parseURIContent(uri);
			return ok(Operations.verifyAndUpdateElement(payload, username));
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
	public Response deleteElement(@PathParam("username") String username, @PathParam("id") String elementId) {

		UserService userService = getUserService();
		StorageRequests requestor = getPipelineElementRdfStorage();
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
	public Response getActionAsJsonLd(@PathParam("id") String elementId)
	{
		StorageRequests requestor = getPipelineElementRdfStorage();
		elementId = decode(elementId);
		try {
			if (requestor.getSEPAById(elementId) != null) return ok(toJsonLd(requestor.getSEPAById(elementId)));
			else if (requestor.getSEPById(elementId) != null) return ok(toJsonLd(requestor.getSEPById(elementId)));
			else if (requestor.getSECById(elementId) != null) return ok(toJsonLd(requestor.getSECById(elementId)));
			else return ok(Notifications.create(NotificationType.UNKNOWN_ERROR));
		} catch (URISyntaxException e) {
			return constructErrorMessage(new Notification(NotificationType.UNKNOWN_ERROR.title(), NotificationType.UNKNOWN_ERROR.description(), e.getMessage()));
		}
	}
}
