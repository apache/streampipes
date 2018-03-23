/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.rest.impl;

import org.streampipes.model.client.messages.Notifications;
import org.streampipes.storage.api.IPipelineCategoryStorage;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/v2/users/{username}/pipelinecategories")
public class PipelineCategory extends AbstractRestInterface {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCategories(@PathParam("username") String username) {
		return ok(getPipelineCategoryStorage()
				.getPipelineCategories());
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response addCategory(@PathParam("username") String username, org.streampipes.model.client.pipeline.PipelineCategory pipelineCategory) {
		boolean success = getPipelineCategoryStorage()
				.addPipelineCategory(pipelineCategory);
		if (success) return ok(Notifications.success("Category successfully stored. "));
		else return ok(Notifications.error("Could not create category."));
	}
	
	@DELETE
	@Path("/{categoryId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeCategory(@PathParam("username") String username, @PathParam("categoryId") String categoryId) {
		boolean success = getPipelineCategoryStorage()
				.deletePipelineCategory(categoryId);
		if (success) return ok(Notifications.success("Category successfully deleted. "));
		else return ok(Notifications.error("Could not delete category."));
	}

	private IPipelineCategoryStorage getPipelineCategoryStorage() {
		return getNoSqlStorage().getPipelineCategoryStorageApi();
	}
}
