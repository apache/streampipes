package de.fzi.cep.sepa.rest.v2;

import java.io.File;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sun.jersey.multipart.FormDataParam;

import de.fzi.cep.sepa.manager.generation.ArchetypeManager;
import de.fzi.cep.sepa.model.client.deployment.DeploymentConfiguration;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.util.GsonSerializer;
import de.fzi.cep.sepa.rest.api.AbstractRestInterface;


@Path("/v2/users/{username}/deploy")
public class DeploymentImpl extends AbstractRestInterface {

	@POST
	@Path("/storm")
	@Produces("application/zip")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response getFile(@FormDataParam("config") String deploymentConfig, @FormDataParam("model") String model) {
	    
		DeploymentConfiguration config = fromJson(deploymentConfig, DeploymentConfiguration.class);
		
		SepaDescription sepa = GsonSerializer.getGsonWithIds().fromJson(model, SepaDescription.class);
		
		File f = new ArchetypeManager(config, sepa).getGeneratedFile();

	    if (!f.exists()) {
	        throw new WebApplicationException(404);
	    }

	    return Response.ok(f)
	            .header("Content-Disposition",
	                    "attachment; filename=" +f.getName()).build();
	}
}
