package de.fzi.cep.sepa.rest.v2;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.openrdf.rio.RDFFormat;

import com.sun.jersey.multipart.FormDataParam;

import de.fzi.cep.sepa.messages.Notifications;
import de.fzi.cep.sepa.rest.api.AbstractRestInterface;
import de.fzi.cep.sepa.rest.api.v2.Context;
import de.fzi.cep.sepa.storage.controller.StorageManager;
import de.fzi.sepa.model.client.util.Utils;

@Path("/v2/contexts")
public class ContextImpl extends AbstractRestInterface implements Context {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public String getAvailableContexts() {
		return toJson(StorageManager.INSTANCE.getContextStorage().getAvailableContexts());
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Override
	public String addContext(@FormDataParam("file") InputStream inputFile, @FormDataParam("context") String json) {
		
		de.fzi.cep.sepa.model.client.ontology.Context contextInfo = fromJson(json, de.fzi.cep.sepa.model.client.ontology.Context.class);
		contextInfo.setInputStream(inputFile);
		
		boolean success = StorageManager.INSTANCE.getContextStorage().addContext(contextInfo);
		if (success) return toJson(Notifications.success("Context successfully added."));
		else return toJson(Notifications.error("Could not add context."));
	}

	@DELETE
	@Path("/{contextId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public String deleteContext(@PathParam("contextId") String contextId) {
		boolean success = StorageManager.INSTANCE.getContextStorage().deleteContext(contextId);
		if (success) return toJson(Notifications.success("Context successfully deleted."));
		else return toJson(Notifications.error("Could not delete context."));
	}

}
