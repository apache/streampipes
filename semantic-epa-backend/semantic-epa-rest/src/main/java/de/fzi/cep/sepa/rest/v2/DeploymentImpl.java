package de.fzi.cep.sepa.rest.v2;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.openrdf.rio.RDFHandlerException;

import com.clarkparsia.empire.annotation.InvalidRdfException;
import com.sun.jersey.multipart.FormDataParam;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.commons.config.ConfigurationManager;
import de.fzi.cep.sepa.commons.exceptions.SepaParseException;
import de.fzi.cep.sepa.manager.generation.ArchetypeManager;
import de.fzi.cep.sepa.manager.operations.Operations;
import de.fzi.cep.sepa.messages.Message;
import de.fzi.cep.sepa.messages.Notifications;
import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.model.client.deployment.DeploymentConfiguration;
import de.fzi.cep.sepa.model.impl.graph.SecDescription;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.transform.JsonLdTransformer;
import de.fzi.cep.sepa.model.util.GsonSerializer;
import de.fzi.cep.sepa.rest.api.AbstractRestInterface;


@Path("/v2/users/{username}/deploy")
public class DeploymentImpl extends AbstractRestInterface {

	@POST
	@Path("/implementation")
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
	
	@POST
	@Path("/import")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String directImport(@PathParam("username") String username, @FormDataParam("config") String deploymentConfig, @FormDataParam("model") String model) {
	    
		DeploymentConfiguration config = fromJson(deploymentConfig, DeploymentConfiguration.class);
		
		SepDescription sep = new SepDescription(GsonSerializer.getGsonWithIds().fromJson(model, SepDescription.class));
		try {
			Message message = Operations.verifyAndAddElement(Utils.asString(new JsonLdTransformer().toJsonLd(sep)), username, true);
			 return toJson(message);
		} catch (RDFHandlerException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| SecurityException | ClassNotFoundException
				| SepaParseException | InvalidRdfException e) {
			e.printStackTrace();
			return toJson(Notifications.error("Error: Could not store source definition."));
		}	   
	}
	
	@POST
	@Path("/description")
	@Produces("application/json")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response getDescription(@FormDataParam("config") String deploymentConfig, @FormDataParam("model") String model) {
		
		DeploymentConfiguration config = fromJson(deploymentConfig, DeploymentConfiguration.class);
		Class<? extends NamedSEPAElement> targetClass;
		
		if (config.getElementType().equals("Sepa")) targetClass = SepaDescription.class;
		else if (config.getElementType().equals("Sec")) targetClass = SecDescription.class; 
		else targetClass = SepDescription.class;
		
		NamedSEPAElement element = GsonSerializer.getGsonWithIds().fromJson(model, targetClass);
		
		File file = new File(ConfigurationManager.getStreamPipesConfigFileLocation() +RandomStringUtils.randomAlphabetic(8) +File.separator +makeName(element.getName()) +".jsonld");
		
		try {
			FileUtils.write(file, Utils.asString(new JsonLdTransformer().toJsonLd(element)));
			return Response.ok(file).header("Content-Disposition",
	                "attachment; filename=" +file.getName()).build();
		} catch (RDFHandlerException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| SecurityException | ClassNotFoundException | IOException
				| InvalidRdfException e) {
			e.printStackTrace();
			return Response.serverError().build();
		}	
	}
	
	private String makeName(String name)
	{
		return name.replaceAll(" ", "_").toLowerCase();
	}
}
