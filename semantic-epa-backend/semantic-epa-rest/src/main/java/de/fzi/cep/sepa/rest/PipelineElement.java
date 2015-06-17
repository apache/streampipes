package de.fzi.cep.sepa.rest;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.fzi.cep.sepa.commons.exceptions.SepaParseException;
import de.fzi.cep.sepa.manager.operations.Operations;
import de.fzi.cep.sepa.messages.Notification;
import de.fzi.cep.sepa.messages.NotificationType;
import de.fzi.cep.sepa.rest.api.AbstractRestInterface;

@Path("/element")
public class PipelineElement extends AbstractRestInterface {

	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public String addElement(@FormParam("uri") String uri)
	{
		try {
			String payload = parseURIContent(uri);
			return toJson(Operations.verifyAndAddElement(payload));
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
			return constructErrorMessage(new Notification(NotificationType.PARSE_ERROR.title(), NotificationType.PARSE_ERROR.description(), e.getMessage()));
		} catch (SepaParseException e) {
			e.printStackTrace();
			return constructErrorMessage(new Notification(NotificationType.PARSE_ERROR.title(), NotificationType.PARSE_ERROR.description(), e.getMessage()));
		}
	}
	
	public static void main(String[] args)
	{
		new PipelineElement().addElement("http://localhost:8090/sepa/eventrate");
	}
}
