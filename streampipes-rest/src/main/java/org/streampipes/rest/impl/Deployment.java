package org.streampipes.rest.impl;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.eclipse.rdf4j.rio.RDFHandlerException;

import com.clarkparsia.empire.annotation.InvalidRdfException;

import org.streampipes.commons.Utils;
import org.streampipes.commons.exceptions.SepaParseException;
import org.streampipes.manager.operations.Operations;
import org.streampipes.model.client.messages.Message;
import org.streampipes.model.client.messages.Notifications;
import org.streampipes.model.NamedSEPAElement;
import org.streampipes.model.client.deployment.DeploymentConfiguration;
import org.streampipes.model.client.deployment.ElementType;
import org.streampipes.model.impl.graph.SecDescription;
import org.streampipes.model.impl.graph.SepDescription;
import org.streampipes.model.impl.graph.SepaDescription;
import org.streampipes.model.transform.JsonLdTransformer;
import org.streampipes.model.util.GsonSerializer;
import org.streampipes.storage.controller.StorageManager;
import org.streampipes.codegeneration.api.CodeGenerator;


@Path("/v2/users/{username}/deploy")
public class Deployment extends AbstractRestInterface {

    @POST
    @Path("/implementation")
    @Produces("application/zip")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response getFile(@FormDataParam("config") String config, @FormDataParam("model") String model) {
        DeploymentConfiguration deploymentConfig = fromJson(config);

        NamedSEPAElement element = getElement(deploymentConfig, model);

        if (element == null) {
            throw new WebApplicationException(500);
        }

        File f = CodeGenerator.getCodeGenerator(deploymentConfig, element).getGeneratedFile();

        if (!f.exists()) {
            throw new WebApplicationException(404);
        }

        return Response.ok(f)
                .header("Content-Disposition",
                        "attachment; filename=" + f.getName()).build();
    }

    public static NamedSEPAElement getElement(DeploymentConfiguration config, String model) {

        if (config.getElementType() == ElementType.SEP) {
            return GsonSerializer.getGsonWithIds().fromJson(model, SepDescription.class);
        } else if (config.getElementType() == ElementType.SEPA) {
            return GsonSerializer.getGsonWithIds().fromJson(model, SepaDescription.class);
        } else if (config.getElementType() == ElementType.SEC) {
            return GsonSerializer.getGsonWithIds().fromJson(model, SecDescription.class);
        } else {
            return null;
        }
    }

    @POST
    @Path("/import")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response directImport(@PathParam("username") String username, @FormDataParam("config") String config, @FormDataParam("model") String model) {

        SepDescription sep = new SepDescription(GsonSerializer.getGsonWithIds().fromJson(model, SepDescription.class));
        try {
            Message message = Operations.verifyAndAddElement(Utils.asString(new JsonLdTransformer().toJsonLd(sep)), username, true);
            return ok(message);
        } catch (RDFHandlerException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException
                | SecurityException | ClassNotFoundException
                | SepaParseException | InvalidRdfException e) {
            e.printStackTrace();
            return ok(Notifications.error("Error: Could not store source definition."));
        }
    }

    @POST
    @Path("/update")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response directUpdate(@PathParam("username") String username, @FormDataParam("config") String config, @FormDataParam("model") String model) {

        DeploymentConfiguration deploymentConfig = fromJson(config);

        boolean success;

        if (deploymentConfig.getElementType().equals("Sepa")) {
            SepaDescription sepa = GsonSerializer.getGsonWithIds().fromJson(model, SepaDescription.class);
            success = StorageManager.INSTANCE.getStorageAPI().deleteSEPA(sepa.getElementId());
            StorageManager.INSTANCE.getStorageAPI().storeSEPA(sepa);
        } else {
            SecDescription sec = new SecDescription(GsonSerializer.getGsonWithIds().fromJson(model, SecDescription.class));
            success = StorageManager.INSTANCE.getStorageAPI().update(sec);
        }

        if (success) return ok(Notifications.success("Element description updated."));
        else return ok(Notifications.error("Could not update element description."));

    }

    @POST
    @Path("/description/java")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response getDescription(@FormDataParam("config") String config, @FormDataParam("model") String model) {

        DeploymentConfiguration deploymentConfig = fromJson(config);

        NamedSEPAElement element = getElement(deploymentConfig, model);

        String java = CodeGenerator.getCodeGenerator(deploymentConfig, element).getDeclareModel();

        try {

            return Response.ok(java).build();
        } catch (IllegalArgumentException | SecurityException e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
    }

    @POST
    @Path("/description/jsonld")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response getDescriptionAsJsonLd(@FormDataParam("config") String config, @FormDataParam("model") String model) {

        DeploymentConfiguration deploymentConfig = fromJson(config);

        NamedSEPAElement element = getElement(deploymentConfig, model);

        try {
            return Response.ok(Utils.asString(new JsonLdTransformer().toJsonLd(element))).build();
        } catch (RDFHandlerException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException
                | SecurityException | ClassNotFoundException
                | InvalidRdfException e) {
            return Response.serverError().build();
        }

    }

    private DeploymentConfiguration fromJson(String config) {
        return GsonSerializer.getGson().fromJson(config, DeploymentConfiguration.class);
    }

    private String makeName(String name) {
        return name.replaceAll(" ", "_").toLowerCase();
    }
}
