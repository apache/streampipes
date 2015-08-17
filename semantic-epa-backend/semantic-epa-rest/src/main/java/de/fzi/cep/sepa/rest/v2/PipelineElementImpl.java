package de.fzi.cep.sepa.rest.v2;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.fzi.cep.sepa.commons.exceptions.SepaParseException;
import de.fzi.cep.sepa.manager.operations.Operations;
import de.fzi.cep.sepa.messages.Notification;
import de.fzi.cep.sepa.messages.NotificationType;
import de.fzi.cep.sepa.messages.Notifications;
import de.fzi.cep.sepa.rest.api.AbstractRestInterface;

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
}
