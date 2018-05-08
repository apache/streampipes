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

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.streampipes.storage.management.StorageManager;
import org.streampipes.model.client.messages.Notifications;
import org.streampipes.model.client.ontology.Context;
import org.streampipes.rest.api.IOntologyContext;

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
