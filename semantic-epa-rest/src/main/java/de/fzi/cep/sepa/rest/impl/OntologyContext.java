package de.fzi.cep.sepa.rest.impl;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataParam;

import de.fzi.cep.sepa.model.client.messages.Notifications;
import de.fzi.cep.sepa.rest.api.IOntologyContext;
import de.fzi.cep.sepa.storage.controller.StorageManager;

@Path("/v2/contexts")
public class OntologyContext extends AbstractRestInterface implements IOntologyContext {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public Response getAvailableContexts() {
		return ok(StorageManager
				.INSTANCE
				.getContextStorage()
				.getAvailableContexts());
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Override
	public Response addContext(@FormDataParam("file") InputStream inputFile, @FormDataParam("context") de.fzi.cep.sepa.model.client.ontology.Context contextInfo) {
		
		contextInfo.setInputStream(inputFile);
		
		boolean success = StorageManager
                .INSTANCE
                .getContextStorage()
                .addContext(contextInfo);
		if (success) {
			return ok(Notifications.success("Context successfully added."));
		}
		else {
			return ok(Notifications.error("Could not add context."));
		}
	}

	@DELETE
	@Path("/{contextId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public Response deleteContext(@PathParam("contextId") String contextId) {
		boolean success = StorageManager.INSTANCE.getContextStorage().deleteContext(contextId);
		if (success) {
			return ok(Notifications.success("Context successfully deleted."));
		}
		else {
			return ok(Notifications.error("Could not delete context."));
		}
	}

}
