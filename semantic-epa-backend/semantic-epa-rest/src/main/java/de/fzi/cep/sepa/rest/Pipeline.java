package de.fzi.cep.sepa.rest;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/pipelines")
public class Pipeline {
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public String addPipelines(String pipeline)
	{
	
		System.out.println(pipeline);
		return "success";
	}

}
