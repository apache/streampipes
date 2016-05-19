package de.fzi.cep.sepa.client.container.rest;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/{elementId}")
public class PipelineElement {

	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getDescription(@PathParam("elementId") String elementId) {
		return null;
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public String invokeRuntime(@PathParam("elementId") String elementId, String invocationGraph) {
		return null;
	}
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Path("/{invocationId}")
	public String getHtml(@PathParam("elementId") String elementId, @PathParam("invocationId") String invocationId) {
		return null;
	}
	
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{invocationId}")
	public String detachRuntime(@PathParam("elementId") String elementId, @PathParam("invocationId") String invocationId) {
		return null;
	}
	
}
