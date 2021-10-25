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

import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.message.NotificationType;
import org.apache.streampipes.rest.api.IPipelineElement;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;
import org.apache.streampipes.rest.shared.util.SpMediaType;
import org.apache.streampipes.storage.couchdb.utils.Filter;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("/v2/sepas")
public class DataProcessorResource extends AbstractAuthGuardedRestResource implements IPipelineElement {

	@GET
	@Path("/available")
	@Produces(MediaType.APPLICATION_JSON)
	@JacksonSerialized
	@Override
	public Response getAvailable() {
		List<DataProcessorDescription> sepas = Filter.byUri(getPipelineElementRdfStorage().getAllDataProcessors(),
				getUserService().getAvailableSepaUris(getAuthenticatedUsername()));
		return ok(sepas);
	}

	@GET
	@Path("/own")
	@JacksonSerialized
	@Produces({MediaType.APPLICATION_JSON, SpMediaType.JSONLD})
	@Override
	public Response getOwn() {
		List<DataProcessorDescription> sepas = Filter.byUri(getPipelineElementRdfStorage().getAllDataProcessors(),
				getUserService().getOwnSepaUris(getAuthenticatedUsername()));
		List<DataProcessorInvocation> si = sepas
						.stream()
						.map(s -> new DataProcessorInvocation(new DataProcessorInvocation(s)))
						.collect(Collectors.toList());

		return ok(si);
	}
	
	@DELETE
	@Path("/own/{elementId}")
	@Produces(MediaType.APPLICATION_JSON)
	@JacksonSerialized
	@Override
	public Response removeOwn(@PathParam("elementId") String elementId) {
		getUserService().deleteOwnSepa(getAuthenticatedUsername(), elementId);
		getPipelineElementRdfStorage().deleteDataProcessor(getPipelineElementRdfStorage().getDataProcessorById(elementId));
		return constructSuccessMessage(NotificationType.STORAGE_SUCCESS.uiNotification());
	}

	@Path("/{elementUri}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@JacksonSerialized
	@Override
	public Response getElement(@PathParam("elementUri") String elementUri) {
		// TODO Access rights
		return ok(new DataProcessorInvocation(new DataProcessorInvocation(getPipelineElementRdfStorage().getDataProcessorById(elementUri))));
	}

}
