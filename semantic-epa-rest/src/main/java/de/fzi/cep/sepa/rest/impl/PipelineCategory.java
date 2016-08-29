package de.fzi.cep.sepa.rest.impl;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.fzi.cep.sepa.messages.Notifications;
import de.fzi.cep.sepa.storage.controller.StorageManager;
import de.fzi.sepa.model.client.util.Utils;

@Path("/v2/users/{username}/pipelinecategories")
public class PipelineCategory extends AbstractRestInterface {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getCategories(@PathParam("username") String username) {
		return Utils.getGson().toJson(StorageManager.INSTANCE.getPipelineCategoryStorageApi().getPipelineCategories());
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public String addCategory(@PathParam("username") String username, String pipelineCategory) {
		de.fzi.cep.sepa.model.client.PipelineCategory category = fromJson(pipelineCategory, de.fzi.cep.sepa.model.client.PipelineCategory.class);
		boolean success = StorageManager.INSTANCE.getPipelineCategoryStorageApi().addPipelineCategory(category);
		if (success) return toJson(Notifications.success("Category successfully stored. "));
		else return toJson(Notifications.error("Could not create category."));
	}
	
	@DELETE
	@Path("/{categoryId}")
	@Produces(MediaType.APPLICATION_JSON)
	public String removeCategory(@PathParam("username") String username, @PathParam("categoryId") String categoryId) {
		boolean success = StorageManager.INSTANCE.getPipelineCategoryStorageApi().deletePipelineCategory(categoryId);
		if (success) return toJson(Notifications.success("Category successfully deleted. "));
		else return toJson(Notifications.error("Could not delete category."));
	}
}
