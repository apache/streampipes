/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.rest.impl;

import org.apache.streampipes.model.client.ontology.Context;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.storage.management.StorageManager;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;

@Path("/v2/contexts")
public class OntologyContext extends AbstractRestInterface {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAvailableContexts() {
		return ok(StorageManager
				.INSTANCE
				.getContextStorage()
				.getAvailableContexts());
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
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
