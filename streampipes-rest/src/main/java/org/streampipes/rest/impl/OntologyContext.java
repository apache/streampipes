package org.streampipes.rest.impl;

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

import org.streampipes.model.client.messages.Notifications;
import org.streampipes.rest.api.IOntologyContext;
import org.streampipes.storage.controller.StorageManager;
import org.streampipes.model.client.ontology.Context;

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
	public Response addContext(@FormDataParam("file") InputStream inputFile, @FormDataParam("context") Context contextInfo) {
		
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
