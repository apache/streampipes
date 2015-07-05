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
import de.fzi.cep.sepa.manager.verification.extractor.TypeExtractor;
import de.fzi.cep.sepa.messages.Notification;
import de.fzi.cep.sepa.messages.NotificationType;
import de.fzi.cep.sepa.model.impl.graph.SecDescription;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.rest.api.AbstractRestInterface;
import de.fzi.cep.sepa.storage.controller.StorageManager;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/element")
public class PipelineElement extends AbstractRestInterface {

	static Logger  LOG = LoggerFactory.getLogger(PipelineElement.class);

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

	private String getElementId(String uri) {
		try {
			String payload = parseURIContent(uri);
			return payload;
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args)
	{
		//new PipelineElement().addElement("http://localhost:8090/sepa/eventrate");
		new PipelineElement().addElement("http://localhost:8091/jms");
		LOG.info(new PipelineElement().getElementId("http://localhost:8091/jms"));
	}
}
