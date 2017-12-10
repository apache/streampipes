package org.streampipes.rest.impl;

import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.streampipes.codegeneration.api.CodeGenerator;
import org.streampipes.commons.Utils;
import org.streampipes.commons.exceptions.SepaParseException;
import org.streampipes.empire.core.empire.annotation.InvalidRdfException;
import org.streampipes.manager.operations.Operations;
import org.streampipes.model.base.NamedStreamPipesEntity;
import org.streampipes.model.client.deployment.DeploymentConfiguration;
import org.streampipes.model.client.deployment.ElementType;
import org.streampipes.model.client.messages.Message;
import org.streampipes.model.client.messages.Notifications;
import org.streampipes.model.graph.DataSinkDescription;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.serializers.jsonld.JsonLdTransformer;
import org.streampipes.serializers.json.GsonSerializer;
import org.streampipes.storage.controller.StorageManager;

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


@Path("/v2/users/{username}/deploy")
public class Deployment extends AbstractRestInterface {

    @POST
    @Path("/implementation")
    @Produces("application/zip")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response getFile(@FormDataParam("config") String config, @FormDataParam("model") String model) {
        DeploymentConfiguration deploymentConfig = fromJson(config);

        NamedStreamPipesEntity element = getElement(deploymentConfig, model);

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

    public static NamedStreamPipesEntity getElement(DeploymentConfiguration config, String model) {

        if (config.getElementType() == ElementType.SEP) {
            return GsonSerializer.getGsonWithIds().fromJson(model, DataSourceDescription.class);
        } else if (config.getElementType() == ElementType.SEPA) {
            return GsonSerializer.getGsonWithIds().fromJson(model, DataProcessorDescription.class);
        } else if (config.getElementType() == ElementType.SEC) {
            return GsonSerializer.getGsonWithIds().fromJson(model, DataSinkDescription.class);
        } else {
            return null;
        }
    }

    @POST
    @Path("/import")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response directImport(@PathParam("username") String username, @FormDataParam("config") String config, @FormDataParam("model") String model) {

        DataSourceDescription sep = new DataSourceDescription(GsonSerializer.getGsonWithIds().fromJson(model, DataSourceDescription.class));
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
            DataProcessorDescription sepa = GsonSerializer.getGsonWithIds().fromJson(model, DataProcessorDescription.class);
            success = StorageManager.INSTANCE.getStorageAPI().deleteSEPA(sepa.getElementId());
            StorageManager.INSTANCE.getStorageAPI().storeSEPA(sepa);
        } else {
            DataSinkDescription sec = new DataSinkDescription(GsonSerializer.getGsonWithIds().fromJson(model, DataSinkDescription.class));
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

        NamedStreamPipesEntity element = getElement(deploymentConfig, model);

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

        NamedStreamPipesEntity element = getElement(deploymentConfig, model);

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
