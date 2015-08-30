package de.fzi.cep.sepa.rest.v2;

import java.io.File;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import de.fzi.cep.sepa.manager.generation.ArchetypeGenerator;


@Path("/v2/users/{username}/deploy")
public class DeploymentImpl {

	@GET
	@Path("/storm")
	@Produces("application/zip")
	public Response getFile() {
	    
		ArchetypeGenerator gen = new ArchetypeGenerator("de.fzi.test", "test-test", "TestTest");
		
		File f = gen.toZip();

	    if (!f.exists()) {
	        throw new WebApplicationException(404);
	    }

	    return Response.ok(f)
	            .header("Content-Disposition",
	                    "attachment; filename=" +f.getName()).build();
	}
}
